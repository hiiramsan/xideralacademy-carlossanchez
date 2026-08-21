package com.luv2code.springboot.cruddemo.rest;

import com.luv2code.springboot.cruddemo.entity.Product;
import tools.jackson.databind.json.JsonMapper;
import com.luv2code.springboot.cruddemo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductRestController {

    private ProductService productService;

    private JsonMapper jsonMapper;

    @Autowired
    public ProductRestController(ProductService theProductService, JsonMapper theJsonMapper) {
        productService = theProductService;
        jsonMapper = theJsonMapper;
    }

    // expose "/products" and return a list of employees
    @GetMapping("/products")
    public List<Product> findAll() {
        return productService.findAll();
    }

    // add mapping for GET /employees/{employeeId}
    //
    // El employeeId ahora es String: un ObjectId de MongoDB, no un entero.

    @GetMapping("/products/{productId}")
    public Product getEmployee(@PathVariable String productId) {

        Product theProduct = productService.findById(productId);

        if (theProduct == null) {
            throw new RuntimeException("Product id not found - " + productId);
        }

        return theProduct;
    }

    // add mapping for POST /employees - add new employee

    @PostMapping("/products")
    public Product addEmployee(@RequestBody Product theProduct) {

        // also just in case they pass an id in JSON ... set id to null
        // this is to force a save of new item ... instead of update
        //
        // En JPA esto era setId(0). En MongoDB el equivalente es null: si el id
        // viene nulo se inserta un documento nuevo, y si viene con valor se
        // REEMPLAZA el documento que ya existía con ese id.

        theProduct.setId(null);

        Product dbProduct = productService.save(theProduct);

        return dbProduct;
    }

    // add mapping for PUT /employees - update existing employee

    @PutMapping("/products")
    public Product updateEmployee(@RequestBody Product theProduct) {

        Product dbProduct = productService.save(theProduct);

        return dbProduct;
    }

    // add mapping for PATCH /employees/{employeeId} - patch employee ... partial
    // update

    @PatchMapping("/products/{productId}")
    public Product patchEmployee(@PathVariable String productId,
                                 @RequestBody Map<String, Object> patchPayload) {

        // Step 1: Retrieve the existing employee from database
        Product tempProduct = productService.findById(productId);

        if (tempProduct == null) {
            throw new RuntimeException("Employee id not found - " + productId);
        }

        // Step 2: Security check - prevent ID modifications
        // The ID should never change, so reject any attempts to modify it
        if (patchPayload.containsKey("id")) {
            throw new RuntimeException(
                    "Employee id cannot be modified. Remove 'id' from request body.");
        }

        // Step 3: Apply the partial update
        // This creates a NEW employee object with the updates applied
        Product patchedProduct = jsonMapper.updateValue(tempProduct, patchPayload);

        // Step 4: Save the updated employee to database and return it
        Product dbProduct = productService.save(patchedProduct);

        return dbProduct;
    }

    // add mapping for DELETE /employees/{employeeId} - delete employee

    @DeleteMapping("/products/{productId}")
    public String deleteEmployee(@PathVariable String productId) {

        Product tempProduct = productService.findById(productId);

        // throw exception if null

        if (tempProduct == null) {
            throw new RuntimeException("Employee id not found - " + productId);
        }

        productService.deleteById(productId);

        return "Deleted employee id - " + productId;
    }

}
