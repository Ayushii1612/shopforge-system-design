package com.shop.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.jupiter.api.Test;

class DomainArchitectureTest {

    private final JavaClasses shopClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.shop");

    @Test
    void domainTypesDependOnlyOnJavaAndSharedDomainTypes() {
        classes()
                .that().resideInAPackage("com.shop..domain..")
                .should().onlyDependOnClassesThat()
                .resideInAnyPackage("java..", "com.shop.shared.domain..")
                .check(shopClasses);
    }
}