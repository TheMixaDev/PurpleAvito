import { ref } from 'vue'
import { defineStore } from 'pinia'
import { TreeService } from '@/services/TreeService';

function treeToDictionary(tree, dictionary) {
    for(let i of tree.subNodes) {
        dictionary[i.id] = i.id + " - " + i.name;
        treeToDictionary(i, dictionary);
    }
    if(tree.id != 0)
        tree.id = tree.name
    return dictionary;
}

export const useTreeStore = defineStore('tree', () => {
    const microcategories = ref({});
    const locations = ref({});

    const minMicrocategory = ref(1);
    const maxMicrocategory = ref(61);
    const minLocation = ref(1);
    const maxLocation = ref(4108);

    function get() {
        if(!microcategories.value[1] || microcategories.value[1] == 1) {
            TreeService.getMicrocategories(foundMicrocategories => {
                microcategories.value = treeToDictionary(foundMicrocategories, {});
                TreeService.getLocations(foundLocations => {
                    locations.value = treeToDictionary(foundLocations, {});
                }, () => {
                    for(let i=1;i<=4108;i++) {
                        if(!locations.value[i])
                            locations.value[i] = i;
                    }
                })
            }, () => {
                for(let i=1;i<=61;i++) {
                    if(!locations.value[i])
                        locations.value[i] = i;
                }
            })
        }
    }

    return { 
        microcategories,
        locations,
        get,

        minMicrocategory,
        maxMicrocategory,
        minLocation,
        maxLocation
    };
});