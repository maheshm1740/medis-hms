package com.medis.hospital_system.infrastructure.cache;

public final class CacheNames {

    private CacheNames() {}

    // Single-entity caches
    public static final String USERS        = "users";
    public static final String DOCTORS      = "doctors";
    public static final String PATIENTS     = "patients";
    public static final String APPOINTMENTS = "appointments";
    public static final String MEDICINES    = "medicines";
    public static final String INVENTORY    = "inventory";

    // List-level caches
    public static final String DOCTORS_ALL              = "doctors_all";
    public static final String MEDICINES_ALL            = "medicines_all";
    public static final String APPOINTMENTS_BY_DOCTOR   = "appointments_by_doctor";
    public static final String APPOINTMENTS_BY_PATIENT  = "appointments_by_patient";
    public static final String APPOINTMENTS_BY_STATUS   = "appointments_by_status";
    public static final String DOCTORS_BY_SPEC          = "doctors_by_spec";
}