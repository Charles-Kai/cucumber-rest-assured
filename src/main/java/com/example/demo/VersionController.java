
package com.example.demo;

import com.example.demo.model.VersionInline;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * No description
 * (Generated with springmvc-raml-parser v.2.0.5)
 * 
 */
@RestController
@RequestMapping("/api/version")
@Validated
public class VersionController {


    /**
     * No description
     * 
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<VersionInline> getVersionInlineByVersion(
        @RequestParam
        String version) {
        return null; //TODO Autogenerated Method Stub. Implement me please.
    }

}
