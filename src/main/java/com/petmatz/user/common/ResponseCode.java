package com.petmatz.user.common;

public interface ResponseCode {
    String SUCCESS = "SU";

    String VALIDATION_FAIL = "VF";

    String DUPLICATE_ID = "DI";

    String SIGN_IN_FAIL = "SF";

    String CERTIFICATION_FAIL = "CF";

    String MAIL_FAIL="MF";

    String DATABASE_ERROR = "DBE";

    String ID_NOT_FOUND = "IDNF";

    String ID_NOT_MATCHING = "IDNM";

    String WRONG_ROLE = "WR";

    String WRONG_PASSWORD="WP";

    String EDIT_FAIL = "EF";

    String HEARTED_ID_NOT_FOUND = "HINF";
}