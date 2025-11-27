package com.smartshop.dto.requist.updateRequistDto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDTO {

    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String nom;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have maximum 10 integer digits and 2 decimal digits")
    private BigDecimal prixUnitaire;

    @DecimalMin(value = "0", message = "Stock must be 0 or greater")
    @Digits(integer = 10, fraction = 2, message = "Stock must have maximum 10 integer digits and 2 decimal digits")
        private BigDecimal stockDisponible;
}

