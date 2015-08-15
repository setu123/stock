package com.mycompany.service;

import com.mycompany.model.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @date May 13, 2015
 * @author Setu
 */
public class CustomHashMap extends HashMap<String, List<Item>>{
    public List<Item> getItems(String code){
        List<Item> items = get(code);
        if(items == null)
            items = new ArrayList<>();
        return items;
    }
    
    public void putItem(Item item){
        List<Item> items = getItems(item.getCode());
        items.add(item);
        put(item.getCode(), items);
    }

}
