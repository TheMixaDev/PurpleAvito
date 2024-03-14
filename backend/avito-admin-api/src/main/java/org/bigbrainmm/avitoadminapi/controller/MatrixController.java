package org.bigbrainmm.avitoadminapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bigbrainmm.avitoadminapi.StaticStorage;
import org.bigbrainmm.avitoadminapi.dto.*;
import org.bigbrainmm.avitoadminapi.entity.DiscountBaseline;
import org.bigbrainmm.avitoadminapi.dto.DiscountSegment;
import org.bigbrainmm.avitoadminapi.entity.History;
import org.bigbrainmm.avitoadminapi.entity.SourceBaseline;
import org.bigbrainmm.avitoadminapi.repository.DiscountBaselineRepository;
import org.bigbrainmm.avitoadminapi.repository.HistoryRepository;
import org.bigbrainmm.avitoadminapi.repository.SourceBaselineRepository;
import org.bigbrainmm.avitoadminapi.service.SOCDelegatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import static org.bigbrainmm.avitoadminapi.StaticStorage.*;

/**
 * Контроллер для работы с матрицами. Просмотр подробностей и тестирование в swagger-ui: http://localhost:PORT/swagger-ui/index.html
 */
@RequiredArgsConstructor
@RequestMapping("/api/matrices")
@RestController
@Tag(name = "Работа с матрицами", description = "Тут располагаются методы для установки, обновления, загрузки, клонирования, просмотра и многих других операций над матрицами")
public class MatrixController {

    private final Logger logger = LoggerFactory.getLogger(MatrixController.class);
    private final DiscountBaselineRepository discountBaselineRepository;
    private final SourceBaselineRepository sourceBaselineRepository;
    private final HistoryRepository historyRepository;
    private final SOCDelegatorService socDelegatorService;
    private final JdbcTemplate jdbcTemplate;

    @Value("${DEMO_SERVER}")
    private boolean isDemo;
    @Value("${AUTO_CACHE}")
    private boolean isAutoCache;
    @Value("${USE_HASH}")
    private boolean useHash;
    private final int MAX_SQL_PACKET_SIZE = 100000;

    @GetMapping(produces = "application/json")
    @Operation(summary = "Посмотреть список всех матриц")
    public AllMatrixResponse getMatrices() {
        AllMatrixResponse allMatrixRequest = new AllMatrixResponse();
        allMatrixRequest.setBaselineMatrices(sourceBaselineRepository.findAllisReadyTrue().stream().map(s -> new Matrix(s.getName())).toList());
        allMatrixRequest.setDiscountMatrices(discountBaselineRepository.findAllisReadyTrue().stream().map(s -> new Matrix(s.getName())).toList());
        return allMatrixRequest;
    }

    @GetMapping(value = "/{matrix_name}", produces = "application/json")
    @Operation(summary = "Получить строки матрицы по имени с использованием указанных параметров offset и limit для пагинации")
    public ResponseEntity<MatrixContent> getMatrixRows(
            @PathVariable("matrix_name") String name,
            @RequestParam(value = "offset", required = true) int offset,
            @RequestParam(value = "limit", required = true) int limit
    ) {
        if (sourceBaselineRepository.findByName(name) == null && discountBaselineRepository.findByName(name) == null)
            throw new InvalidDataException("Матрицы с таким именем не существует");
        String sql = "SELECT * FROM " + name + " OFFSET ? LIMIT ?";
        String countQuery = "SELECT COUNT(*) FROM " + name;
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, offset, limit);
        int total = jdbcTemplate.queryForObject(countQuery, Integer.class);
        return ResponseEntity.status(HttpStatus.OK).body(new MatrixContent(rows, total));
    }

    @PostMapping(value = "/{matrix_name}", produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Установить изменения в матрице из CSV файла. discount_matrix_new - создает новую чистую матрицу. data - если пуст, то отправит ошибку.")
    public ResponseEntity<String> setChangesInMatrix(
            @PathVariable("matrix_name") String name,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        if(file == null || file.isEmpty())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Не прикреплен файл изменений матрицы\" }");
        String newName;
        if (name.contains("baseline_matrix")) {
            if (sourceBaselineRepository.findByName(name) == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Матрицы с таким именем не существует\" }");
            newName = getNewNameBaselineMatrix();
        } else if (name.equals("discount_matrix_new")) {
            newName = getNewNameDiscountMatrix();
        } else if (name.contains("discount_matrix")) {
            if (discountBaselineRepository.findByName(name) == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Матрицы с таким именем не существует\" }");
            newName = getNewNameDiscountMatrix();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Неверное имя матрицы\" }");
        }
        SourceBaseline newSourceBaseline = null;
        DiscountBaseline newDiscountBaseline = null;
        boolean completedSuccessfully = false;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            long lines = reader.lines().count();
            if(lines > 300000 && isDemo) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"Вы находитесь на демо-сервере. " +
                        "Демо-сервер не может обрабатывать большие файлы (более 300,000 строк) в связи с ограничением размера жесткого диска арендуемого сервера. " +
                        "Для включения этой возможности выставите DEMO_SERVER=false в переменных окружения сервера админ-панели.\", \"showModal\": true }");
            }
            int counter = 0;
            if (name.equals("discount_matrix_new")) jdbcTemplate.update("create table " + newName + " (id bigint, microcategory_id int, location_id int, price int, found_price int, found_microcategory_id int, found_location_id int);");
            else jdbcTemplate.update("create table " + newName + " as select * from " + name);
            jdbcTemplate.update("CREATE INDEX idx_" + newName + "_hash ON " + newName + " USING hash(id);");
            jdbcTemplate.update("CREATE TRIGGER before_insert_" + newName + " BEFORE INSERT ON " + newName + " FOR EACH ROW EXECUTE FUNCTION set_matrix_id();");

            if (name.contains("baseline_matrix")) {
                newSourceBaseline = new SourceBaseline(newName, false, isAutoCache);
                sourceBaselineRepository.save(newSourceBaseline);
            } else if (name.contains("discount_matrix")) {
                newDiscountBaseline = new DiscountBaseline(newName, false, isAutoCache);
                discountBaselineRepository.save(newDiscountBaseline);
            }
            jdbcTemplate.update("ALTER TABLE " + newName + " ADD CONSTRAINT " + newName + "_pkey PRIMARY KEY (location_id, microcategory_id);");
            try {
                // Заполнение данными
                String insertion = "insert into " + newName + " (microcategory_id, location_id, price) values ";
                StringBuilder query = new StringBuilder(insertion);
                String nullLiteral = "null";
                reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                String row;
                int line = 0;
                while ((row = reader.readLine()) != null) {
                    counter++;
                    var slt = row.split(",");
                    if (     slt.length < 3 ||
                            !((isNumeric(slt[0]) || slt[0].equals(nullLiteral)) &&
                            (isNumeric(slt[1]) || slt[1].equals(nullLiteral)) &&
                            (isNumeric(slt[2]) || slt[2].equals(nullLiteral)))
                    ) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \"В строке номер " + counter + " обнаружена ошибка. " +
                                "Данные не загружены. \", \"showModal\": true }");
                    }
                    query.append("(").append(slt[0]).append(", ").append(slt[1]).append(", ").append(slt[2]).append(")");
                    query.append(", ");
                    line++;
                    if (line == MAX_SQL_PACKET_SIZE) {
                        query.deleteCharAt(query.length() - 2);
                        query.append("ON CONFLICT (microcategory_id, location_id) DO UPDATE SET price = EXCLUDED.price;");
                        jdbcTemplate.update(query.toString());
                        System.out.println("Sent " + line + " rows to " + newName);
                        query = new StringBuilder(insertion);
                        line = 0;
                    }
                }
                if (line > 0) {
                    query.deleteCharAt(query.length() - 2);
                    query.append("ON CONFLICT (microcategory_id, location_id) DO UPDATE SET price = EXCLUDED.price;");
                    jdbcTemplate.update(query.toString());
                    logger.info("Sent " + line + " rows to " + newName);
                    completedSuccessfully = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \" Неверный формат тела запроса. \" }");
            } finally {
                if (completedSuccessfully) {
                    // Кэширование
                    if (isAutoCache) {
                        fillCacheMatrix(newName);
                    }
                    if (name.contains("baseline_matrix")) {
                        newSourceBaseline.setReady(true);
                        sourceBaselineRepository.save(newSourceBaseline);
                    } else if (name.contains("discount_matrix")) {
                        newDiscountBaseline.setReady(true);
                        discountBaselineRepository.save(newDiscountBaseline);
                    }
                } else {
                    // Cleanup if not completed successfully
                    jdbcTemplate.update("drop table " + newName);
                    if (name.contains("baseline_matrix")) {
                        assert newSourceBaseline != null;
                        sourceBaselineRepository.delete(newSourceBaseline);
                    } else if (name.contains("discount_matrix")) {
                        assert newDiscountBaseline != null;
                        discountBaselineRepository.delete(newDiscountBaseline);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{ \"message\": \" Неверный формат тела запроса.\" }");
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                "{ \"message\": \"Матрица " + newName + " склонирована успешно.\", " +
                        "\"matrixName\": \"" + newName + "\"}");
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private String getNewNameDiscountMatrix() {
        Long id = discountBaselineRepository.findMaxId();
        return "discount_matrix_" + (id + 1L);
    }

    private String getNewNameBaselineMatrix() {
        Long id = sourceBaselineRepository.findMaxId();
        return "baseline_matrix_" + (id + 1L);
    }

    @GetMapping(value = "/setup", produces = "application/json")
    @Operation(summary = "Посмотреть текущую стандартную матрицу и список скидочных матриц")
    public BaselineMatrixAndSegments setup() {
        return baselineMatrixAndSegments;
    }

    @PostMapping(value = "/setup/baseline", produces = "application/json")
    @Operation(summary = "Установить текущую стандартную матрицу по имени")
    public ResponseEntity<MessageResponse> setupBaseline(
            @RequestBody SetupMatrixRequest request
    ) {
        SourceBaseline sourceBaseline = sourceBaselineRepository.findByName(request.getName());
        if (sourceBaseline == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse("Матрица с именем " + request.getName() + " не найдена"));
        }
        Matrix oldBaseline = baselineMatrixAndSegments.getBaselineMatrix();
        baselineMatrixAndSegments.setBaselineMatrix(new Matrix(sourceBaseline.getName()));
        String error = socDelegatorService.isAllDelegatorsReadyMessage(baselineMatrixAndSegments);
        if(error != null) {
            baselineMatrixAndSegments.setBaselineMatrix(oldBaseline);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(error));
        }
        // Сохранение изменений
        StaticStorage.saveBaselineAndSegments(baselineMatrixAndSegments);
        historyRepository.save(new History(sourceBaseline.getName(), System.currentTimeMillis()));
        socDelegatorService.sendCurrentBaselineAndSegmentsToSOCs();
        return ResponseEntity.ok(new MessageResponse("Матрица " + request.getName() + " установлена"));
    }

    @PostMapping(value = "/setup/segments", produces = "application/json")
    @Operation(summary = "Установить в дискаунт группах матрицы по id сгемента и name discount_table")
    public ResponseEntity<MessageResponse> setupSegments(
            @RequestBody SetupDiscountSegmentsRequest request
    ) {
        if (request.getDiscountSegments().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Список сегментов пуст"));
        }
        Optional<DiscountSegment> discountSegment;
        DiscountBaseline discountBaseline;
        for (var pair : request.getDiscountSegments()) {
            discountSegment = baselineMatrixAndSegments.getDiscountSegments().stream().filter(ds -> Objects.equals(ds.getId(), pair.getId())).findAny();
            if (discountSegment.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse("Сегмент с идентификатором " + pair.getId() + " не найден"));
            discountBaseline = discountBaselineRepository.findByName(pair.getName());
            if (pair.getName() != null) {
                if (!pair.getName().equals("null")) {
                    if (discountBaseline == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new MessageResponse("Матрица с именем " + pair.getName() + " не найдена"));
                }
            }
        }

        List<History> changes = new ArrayList<>();
        List<DiscountSegment> copy = new ArrayList<>();
        for (var ds : baselineMatrixAndSegments.getDiscountSegments()) copy.add(new DiscountSegment(ds.getId(), ds.getName()));

        for (var pair : request.getDiscountSegments()) {
            DiscountSegment ds = copy.stream().filter(ds1 -> Objects.equals(ds1.getId(), pair.getId())).findAny().get();
            if (pair.getName() == null) ds.setName(null);
            else if (pair.getName().equals("null")) ds.setName(null);
            else ds.setName(pair.getName());
            changes.add(new History(ds.getName(), ds.getId(), System.currentTimeMillis()));
        }
        // Проверка уникальности
        for (var pair : request.getDiscountSegments()) {
            if (pair.getName() == null || pair.getName().equals("null")) continue;
            if (copy.stream().filter(ds1 -> Objects.equals(ds1.getName(), pair.getName())).count() > 1) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse("Нарушена уникальность имён скидочных матриц. 1 - сегмент, одна скидочная матрица или null"));
            }
        }
        // Сохранение изменений
        List<DiscountSegment> oldDiscount = baselineMatrixAndSegments.getDiscountSegments();
        baselineMatrixAndSegments.setDiscountSegments(copy);
        String error = socDelegatorService.isAllDelegatorsReadyMessage(baselineMatrixAndSegments);
        if(error != null) {
            baselineMatrixAndSegments.setDiscountSegments(oldDiscount);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(error));
        }
        StaticStorage.saveBaselineAndSegments(baselineMatrixAndSegments);
        historyRepository.saveAll(changes);
        socDelegatorService.sendCurrentBaselineAndSegmentsToSOCs();
        return ResponseEntity.ok(new MessageResponse("Изменения выполнены успешно"));
    }

    public void fillCacheMatrix(String name) {
        logger.info("Filling matrix " + name);
        String sql = "select * from " + name + " order by id asc";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> row : rows) {
            // Описание алгоритма

            //if row.get("price") == null
                // Смотрим строку на 1 микрокатегорию вверх
                    // если в ней стоит "found_price" - копируем "found_price", "found_location", "found_microcategory"
                // Смотрим строку на 1 локацию вверх (с изначальной микрокатегорией)
                    // если в ней стоит "found_price" - копируем "found_price", "found_location", "found_microcategory"
                // Смотрим строку на 1 локацию и 1 категорию вверх
                    // если в ней стоит "found_price" - копируем "found_price", "found_location", "found_microcategory"
            //else
                // поставить в "found_price", "found_location", "found_microcategory" в виде текущих значений строки

            Integer mic = (Integer) row.get("microcategory_id");
            Integer loc = (Integer) row.get("location_id");
            TreeNode micNode = microCategoryRoot.getById(mic.longValue()),
                    locNode = locationsRoot.getById(loc.longValue());
            Integer price = (Integer) row.get("price");
            if (price == null) {
                TreeNode micNodeParent = micNode.getParent();
                TreeNode locNodeParent = locNode.getParent();
                // if parent == null or parent.getname == "root", то запускаем по parentNodeLoc
                // Если и он нулл, то значит мы дошли до вершины, continue
                if (micNodeParent != null && !micNodeParent.getId().equals(0L)) {
                    var found = getRowByMicAndLoc(name, micNodeParent.getId(), loc);
                    if (found != null) {
                        var fPrice = (Integer) found.get("found_price");
                        if (fPrice != null) {
                            setFountPriceToRow(name, mic, loc, fPrice,
                                    (Integer) found.get("found_microcategory_id"),
                                    (Integer) found.get("found_location_id"));
                            continue;
                        }
                    }
                }
                if (locNodeParent != null && !locNodeParent.getId().equals(0L)) {
                    var found = getRowByMicAndLoc(name, mic, locNodeParent.getId());
                    if (found == null) continue;
                    var fPrice = (Integer) found.get("found_price");
                    if (fPrice != null) {
                        setFountPriceToRow(name, mic, loc, fPrice,
                                (Integer) found.get("found_microcategory_id"),
                                (Integer) found.get("found_location_id"));
                    }
                }
            } else {
                setFountPriceToRow(name, mic, loc, price, mic, loc);
            }
        }
    }

    private void setFountPriceToRow(String name, Integer mic, Integer loc, Integer fPrice, Integer fMic, Integer fLoc) {
        jdbcTemplate.update(
                "update " + name +
                " set found_price = " + fPrice +
                ", found_microcategory_id = " + fMic +
                ", found_location_id = " + fLoc +
                " where microcategory_id = " + mic + " and location_id = " + loc
        );
    }

    private Map<String, Object> getRowByMicAndLoc(String name, long mic, long loc) {
        try {
            // Умнейшая формула вычисления хэшированного id
            long id = useHash ? loc + 4108L * (mic - 1) : -1;
            String sql = useHash ?
                    "select * from " + name + " where id = " + id + ";" :
                    "select * from " + name + " where microcategory_id = " + mic + " and location_id = " + loc + ";";
            return jdbcTemplate.queryForMap(sql);
        } catch (Exception e) {
            return null;
        }
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public MessageResponse error(InvalidDataException ex) {
        return new MessageResponse(ex.getMessage());
    }


    public static class InvalidDataException extends RuntimeException {
        public InvalidDataException(String message) {
            super(message);
        }
    }
}
