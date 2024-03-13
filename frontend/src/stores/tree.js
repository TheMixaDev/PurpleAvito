import { ref } from 'vue'
import { defineStore } from 'pinia'

import { TreeService } from '@/services/TreeService';

/**
 * Recursively converts a tree data structure into a dictionary format.
 *
 * @param {Object} tree - The tree data structure to convert
 * @param {Map} dictionary - The dictionary to store the converted data
 * @return {Map} The dictionary representation of the tree
 */
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

    /**
     * Retrieves data and sets values for microcategories and locations trees if not already set or setted with dummy data.
     */
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