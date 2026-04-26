package com.plmph.pdl;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdlReaderTest {

    private String pdl      = "!0;!1;-888;999;\"abc;address(\"Str;\"123;/123.45;%987.65;){/89.0;.f1;\"v1;.f2;123;'abc;}</89.0;.f1;\"v1;.f2;123;'abc;>[.c1;.c2;'v1;'v2;'v3;'v4;]['v1;'v2;]@2026-12-31T23:59:59.999;";
    private PdlSource pdlSource = new PdlSource().setSource(pdl).setTokenOffsetArray(new long[1024]);

    @Test
    public void testRead() {
        PdlReader pdlReader = new PdlReader();
        pdlReader.init(pdlSource);

        pdlSource.tokenOffsetPairsLength =
                PdlTokenizer.tokenize_for(pdlSource.source, 0, pdlSource.length, pdlSource.tokenOffsetPairs);

        pdlReader.read();
    }

    @Test
    public void testReadAll() {
        PdlReader pdlReader = new PdlReader();
        pdlReader.init(pdlSource);

        pdlSource.tokenOffsetPairsLength = PdlTokenizer.tokenize_for(pdlSource.source, 0, pdlSource.source.length, pdlSource.tokenOffsetPairs);

        pdlReader.instructions.put("address", (args) -> {
            Map address = new HashMap();
            address.put("street", args.get(0));
            address.put("number", args.get(1));
            address.put("f64", args.get(2));
            address.put("f32", args.get(3));
            return address;
        });

        List list = pdlReader.readAll();

        System.out.println("RETURN:");
        System.out.println(list);
    }
}
