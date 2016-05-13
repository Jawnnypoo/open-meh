package com.jawnnypoo.openmeh.shared;

import com.commit451.parcelcheck.ParcelCheckPackageTest;

/**
 * Test all models to make sure they are parcelable
 */
public class AllModelTest extends ParcelCheckPackageTest {

    @Override
    public String[] getModelPackageNames() {
        return new String[] {
                "com.jawnnypoo.openmeh.shared.model"
        };
    }
}
