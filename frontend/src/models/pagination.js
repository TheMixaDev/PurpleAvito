import { computed } from "vue";

export class Pagination {
    constructor(total, itemsPerPage, page) {
        this.total = total;
        this.itemsPerPage = itemsPerPage;
        this.page = page;
    }
    /**
     * Calculates the maximum number of pages based on the total number of items and items per page.
     */
    get maxPage() {
        return computed(() => Math.ceil(this.total / this.itemsPerPage));
    }
    /**
     * Updates the current page to the target page.
     */
    updatePage(targetPage) {
        if(targetPage < 1) return;
        if(targetPage == this.page + 1) return;
        if(targetPage == this.maxPage && this.page == this.maxPage) return;
        if(this.maxPage < 2) return;
        if(targetPage > this.maxPage)
            return this.page = this.maxPage - 1;
        this.page = targetPage - 1;
    }
    /**
     * Calculate the starting index of the current page.
     */
    get start() {
        if(this.end < 1) return 0;
        return this.itemsPerPage * this.page;
    }
    /**
     * Calculates the end index of the current page.
     */
    get end() {
        let counted = (this.page + 1) * this.itemsPerPage;
        return counted < this.total ? counted : this.total;
    }
}