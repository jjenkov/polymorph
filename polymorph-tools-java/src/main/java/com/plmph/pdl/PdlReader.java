package com.plmph.pdl;

import com.plmph.pdl.types.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.function.Function;

public class PdlReader {

    public static final Character TOKEN_TYPE_INSTRUCTION_END = ')';
    public static final Character TOKEN_TYPE_OBJECT_END = '}';
    public static final Character TOKEN_TYPE_TABLE_END = ']';
    public static final Character TOKEN_TYPE_METADATA_END = '>';

    public PdlSource pdlSource = null;
    public int tokenOffsetPairsIndex = 0;

    public Map<String, Function<PdlReader, Object>> instructions = new HashMap<>();

    public PdlReader init(PdlSource pdlSource){
        this.pdlSource = pdlSource;
        return this;
    }

    public List readAll(){
        List tokens = new ArrayList();
        int tokenOffsetPairsLength = pdlSource.tokenOffsetPairsLength;
        while(tokenOffsetPairsIndex < tokenOffsetPairsLength){
            tokens.add(read());
        }
        return tokens;
    }

    public Object read(){
        byte[] source = pdlSource.source;
        long tokenOffsetPair = pdlSource.tokenOffsetPairs[tokenOffsetPairsIndex++];

        int tokenStartOffset = (int) (0xFFFFFFFFL & tokenOffsetPair); // clear top 4 bytes
        int tokenEndOffset   = (int) (tokenOffsetPair >> 32);         // keep  top 4 bytes

        byte tokenType = source[tokenStartOffset];

        Object value = null;
        switch(tokenType){
            case '!' : {
                value = (source[tokenStartOffset + 1] == '1');
                break;
            }
            case '-' : {
                value = Integer.parseInt(new String(source, tokenStartOffset, tokenEndOffset - tokenStartOffset -1)); //todo get rid of new String() in this statement
                break;
            }
            case '+' : {
                value = Integer.parseInt(new String(source, tokenStartOffset + 1, tokenEndOffset - tokenStartOffset -2)); //todo get rid of new String() in this statement
                break;
            }
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' : {
                value = Integer.parseInt(new String(source, tokenStartOffset, tokenEndOffset - tokenStartOffset -1)); //todo get rid of new String() in this statement
                break;
            }
            case '/' : {
                value = Double.parseDouble(new String(source, tokenStartOffset + 1, tokenEndOffset - tokenStartOffset -2));
                break;
            }
            case '%' : {
                value = Float.parseFloat(new String(source, tokenStartOffset + 1, tokenEndOffset - tokenStartOffset -2));
                break;
            }
            case '\'' : {
                value = new String(source, tokenStartOffset + 1, tokenEndOffset - tokenStartOffset - 2, StandardCharsets.US_ASCII);
                break;
            }
            case '"' : {
                value = new String(source, tokenStartOffset + 1, tokenEndOffset - tokenStartOffset - 2, StandardCharsets.UTF_8);
                break;
            }
            case '@' : {
                int tokenLength = tokenEndOffset - tokenStartOffset - 2;
                int year = tokenLength >= 4 ? Integer.parseInt(new String(source, tokenStartOffset + 1, 4)) : 1970;
                int month = tokenLength >= 7 ? Integer.parseInt(new String(source, tokenStartOffset + 6, 2)) : 0;
                int day = tokenLength >= 10 ? Integer.parseInt(new String(source, tokenStartOffset + 9, 2)) : 1;
                int hour = tokenLength >= 13 ? Integer.parseInt(new String(source, tokenStartOffset + 12, 2)) : 0;
                int minute = tokenLength >= 16 ? Integer.parseInt(new String(source, tokenStartOffset + 15, 2)) : 0;
                int second = tokenLength >= 19 ? Integer.parseInt(new String(source, tokenStartOffset + 18, 2)) : 0;
                int millis = tokenLength >= 20 ? Integer.parseInt(new String(source, tokenStartOffset + 21, tokenLength - 20)) : 0;

                value = LocalDateTime.of(year, month, day, hour, minute, second, millis * 1000000);
                break;
            }
            case '.' : {
                value = new PdlKey(source, tokenStartOffset + 1, tokenEndOffset - tokenStartOffset - 2);
                break;
            }
            case '{' : {
                PdlObject object = new PdlObject();

                value = read();
                while(value != TOKEN_TYPE_OBJECT_END){
                    if(value instanceof PdlKey key){
                        value = read();
                        if(value instanceof PdlKey){
                            object.contents.add(new PdlObjectProperty(key, null)); // 2 Keys in a row means a null value for first property
                            //move back one token - to re-read this 2nd key field in next iteration of this loop.
                            tokenOffsetPairsIndex--;
                        } else {
                            object.contents.add(new PdlObjectProperty(key, value));
                        }
                    } else {
                        object.contents.add(value);
                    }
                    value = read();
                }

                value = object;
                break;
            }
            case '}' : {
                value = TOKEN_TYPE_OBJECT_END;
                break;
            }
            case '[' : {
                PdlTable table = new PdlTable();

                value = read();
                while(value != TOKEN_TYPE_TABLE_END && (value instanceof PdlKey)){
                    table.keys.add((PdlKey) value);
                    value = read();
                }
                while(value != TOKEN_TYPE_TABLE_END){
                    table.contents.add(value);
                    value = read();
                }
                value = table;
                break;
            }
            case ']' : {
                value = TOKEN_TYPE_TABLE_END;
                break;
            }
            case '<' : {
                PdlMetadata object = new PdlMetadata();

                value = read();
                while(value != TOKEN_TYPE_METADATA_END){
                    if(value instanceof PdlKey key){
                        value = read();
                        if(value instanceof PdlKey){
                            object.contents.add(new PdlObjectProperty(key, null)); // 2 Keys in a row means a null value for first property
                            //move back one token - to re-read this 2nd key field in next iteration of this loop.
                            tokenOffsetPairsIndex--;
                        } else {
                            object.contents.add(new PdlObjectProperty(key, value));
                        }
                    } else {
                        object.contents.add(value);
                    }
                    value = read();
                }

                value = object;
                break;
            }
            case '>' : {
                value = TOKEN_TYPE_METADATA_END;
                break;
            }
            case 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z': {
                String instruction = new String(source, tokenStartOffset, tokenEndOffset - tokenStartOffset - 1);
                value = instructions.get(instruction).apply(this);
                break;
            }
            case ')' : {
                value = TOKEN_TYPE_INSTRUCTION_END;
                break;
            }
            default: {
                throw new RuntimeException("Unknown token type: [" + tokenType + "]");
            }
        }

        return value;
    }
}
