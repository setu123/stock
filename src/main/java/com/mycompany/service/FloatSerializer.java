/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.service;

import java.io.IOException;
import java.text.DecimalFormat;
import javax.json.stream.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

/**
 *
 * @author setu
 */
public class FloatSerializer extends JsonSerializer<Float> {
    
    private final String pattern = "#.#";
    private final DecimalFormat formatter = new DecimalFormat(pattern);

    @Override
    public void serialize(Float value, JsonGenerator jg, SerializerProvider sp) throws IOException, JsonGenerationException {
        if (value == null) {
            jg.writeNull();
        }else{
            String output = formatter.format(value);
            jg.writeNumber(output);
        }
    }
    
}
