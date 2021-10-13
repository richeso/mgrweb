package com.hpe.mgr;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.hpe.mgr");

        noClasses()
            .that()
            .resideInAnyPackage("com.hpe.mgr.service..")
            .or()
            .resideInAnyPackage("com.hpe.mgr.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..com.hpe.mgr.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
