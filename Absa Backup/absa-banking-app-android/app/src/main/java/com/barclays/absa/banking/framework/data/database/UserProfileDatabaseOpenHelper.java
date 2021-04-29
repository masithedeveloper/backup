/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */
package com.barclays.absa.banking.framework.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.barclays.absa.banking.framework.utils.BMBLogger;

class UserProfileDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "user_profile_db";

    static final String TABLE_USER_PROFILE = "user_profile";
    static final String COLUMN_CUSTOMER_NAME = "CUSTOMER_NAME";
    static final String COLUMN_BACKGROUND_IMAGE_ID = "BACKGROUND_IMAGE_ID";
    static final String COLUMN_IMAGE_NAME = "IMAGE_NAME";
    static final String COLUMN_USER_ID = "USER_ID";
    static final String COLUMN_CLIENT_TYPE = "CLIENT_TYPE";
    static final String COLUMN_LAST_LOGIN_TIMESTAMP = "LAST_LOGIN_TIMESTAMP";
    static final String COLUMN_SURE_CHECK_2_ENABLED = "SURE_CHECK_2_ENABLED";
    static final String COLUMN_LANGUAGE_CODE = "LANGUAGE_CODE";
    static final String COLUMN_MAILBOX_ID = "MAILBOX_ID";
    private static final String TAG = UserProfileDatabaseOpenHelper.class.getSimpleName();
    static final String COLUMN_MIGRATION_VERSION = "COLUMN_MIGRATION_VERSION";
    static final String COLUMN_USER_NUMBER = "COLUMN_USER_NUMBER";
    private final String ADD_2FA_STATE_COLUMN = "ALTER TABLE " + TABLE_USER_PROFILE + " ADD COLUMN " + COLUMN_SURE_CHECK_2_ENABLED + " INTEGER DEFAULT 0";
    private final String ADD_LANGUAGE_CODE_COLUMN = "ALTER TABLE " + TABLE_USER_PROFILE + " ADD COLUMN " + COLUMN_LANGUAGE_CODE + " TEXT";
    private final String ADD_MIGRATION_VERSION_COLUMN = "ALTER TABLE " + TABLE_USER_PROFILE + " ADD COLUMN " + COLUMN_MIGRATION_VERSION + " INTEGER DEFAULT 0";
    private final String ADD_CLIENT_TYPE_COLUMN = "ALTER TABLE " + TABLE_USER_PROFILE + " ADD COLUMN " + COLUMN_CLIENT_TYPE + " TEXT";
    private final String ADD_USER_NUMBER_COLUMN = "ALTER TABLE " + TABLE_USER_PROFILE + " ADD COLUMN " + COLUMN_USER_NUMBER + " INTEGER DEFAULT 0";
    private final String ADD_MAILBOX_ID_COLUMN = "ALTER TABLE " + TABLE_USER_PROFILE + " ADD COLUMN " + COLUMN_MAILBOX_ID + " TEXT";

    UserProfileDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_PROFILES_TABLE = "CREATE TABLE " + TABLE_USER_PROFILE + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + COLUMN_CUSTOMER_NAME + " TEXT,"
                + COLUMN_BACKGROUND_IMAGE_ID + " TEXT,"
                + COLUMN_LAST_LOGIN_TIMESTAMP + " TEXT,"
                + COLUMN_IMAGE_NAME + " TEXT,"
                + COLUMN_SURE_CHECK_2_ENABLED + " INTEGER DEFAULT 0,"
                + COLUMN_LANGUAGE_CODE + " TEXT,"
                + COLUMN_USER_ID + " TEXT,"
                + COLUMN_CLIENT_TYPE + " TEXT,"
                + COLUMN_MIGRATION_VERSION + "  INTEGER DEFAULT 1,"
                + COLUMN_USER_NUMBER + " INTEGER DEFAULT 0,"
                + COLUMN_MAILBOX_ID + " TEXT,"
                + "UNIQUE(" + COLUMN_USER_ID + "))";
        BMBLogger.i(TAG, CREATE_USER_PROFILES_TABLE);
        db.execSQL(CREATE_USER_PROFILES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        BMBLogger.i(UserProfileDatabaseOpenHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion);

        switch (oldVersion) {
            //NB: do not add break statements between cases here; all cases must run for all
            case 1:
                db.execSQL(ADD_2FA_STATE_COLUMN);
                db.execSQL(ADD_LANGUAGE_CODE_COLUMN);
            case 2:
                //do something to migrate v2 to v3
                db.execSQL(ADD_MIGRATION_VERSION_COLUMN);
            case 3:
                //do something to migrate v3 to v4
                db.execSQL(ADD_CLIENT_TYPE_COLUMN);
            case 4:
                db.execSQL(ADD_USER_NUMBER_COLUMN);
            case 5:
                db.execSQL(ADD_MAILBOX_ID_COLUMN);
            case 6:
                //do something to migrate v6 to v7
            default:
                break;
        }
    }
}