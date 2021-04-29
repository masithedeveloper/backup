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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.barclays.absa.banking.boundary.model.UserProfile;
import com.barclays.absa.banking.framework.app.BMBApplication;

import java.util.ArrayList;
import java.util.List;

public class UserProfileDAO {
    private final UserProfileDatabaseOpenHelper userProfileDatabaseOpenHelper;
    private static final UserProfileDAO USER_PROFILE_DAO = new UserProfileDAO();

    private UserProfileDAO() {
        final Context applicationContext = BMBApplication.getInstance();
        userProfileDatabaseOpenHelper = new UserProfileDatabaseOpenHelper(applicationContext);
    }

    public static UserProfileDAO getInstance() {
        return USER_PROFILE_DAO;
    }

    public long createUserProfile(UserProfile userProfile) {
        try (SQLiteDatabase database = userProfileDatabaseOpenHelper.getWritableDatabase()) {
            if (database != null && database.isOpen()) {
                ContentValues values = new ContentValues();
                values.put(UserProfileDatabaseOpenHelper.COLUMN_CUSTOMER_NAME, userProfile.getCustomerName());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_LAST_LOGIN_TIMESTAMP, userProfile.getDateTimestamp());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_IMAGE_NAME, userProfile.getImageName());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_USER_ID, userProfile.getUserId());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_CLIENT_TYPE, userProfile.getClientType());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_SURE_CHECK_2_ENABLED, userProfile.isTwoFAEnabled() ? 1 : 0);
                values.put(UserProfileDatabaseOpenHelper.COLUMN_LANGUAGE_CODE, userProfile.getLanguageCode());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_MIGRATION_VERSION, userProfile.getMigrationVersion());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_USER_NUMBER, userProfile.getUserNumber());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_MAILBOX_ID, userProfile.getMailboxId());
                return database.insert(UserProfileDatabaseOpenHelper.TABLE_USER_PROFILE, null, values);
            } else {
                throw new SQLiteException("No writable databases found");
            }
        }
    }

    public int deleteUserProfile(String randomId) {
        try (SQLiteDatabase database = userProfileDatabaseOpenHelper.getWritableDatabase()) {
            if (database != null && database.isOpen()) {
                String where = UserProfileDatabaseOpenHelper.COLUMN_USER_ID + " = ?";
                String[] whereArgs = {randomId};
                return database.delete(UserProfileDatabaseOpenHelper.TABLE_USER_PROFILE, where, whereArgs);
            } else {
                throw new SQLiteException("No writable databases found");
            }
        }
    }

    public int updateUserProfile(UserProfile userProfile) {
        try (SQLiteDatabase database = userProfileDatabaseOpenHelper.getWritableDatabase()) {
            if (database != null && database.isOpen()) {
                ContentValues values = new ContentValues();
                values.put(UserProfileDatabaseOpenHelper.COLUMN_CUSTOMER_NAME, userProfile.getCustomerName());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_LAST_LOGIN_TIMESTAMP, userProfile.getDateTimestamp());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_IMAGE_NAME, userProfile.getImageName());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_USER_ID, userProfile.getUserId());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_CLIENT_TYPE, userProfile.getClientType());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_SURE_CHECK_2_ENABLED, userProfile.isTwoFAEnabled() ? 1 : 0);
                values.put(UserProfileDatabaseOpenHelper.COLUMN_LANGUAGE_CODE, userProfile.getLanguageCode());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_MIGRATION_VERSION, userProfile.getMigrationVersion());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_USER_NUMBER, userProfile.getUserNumber());
                values.put(UserProfileDatabaseOpenHelper.COLUMN_MAILBOX_ID, userProfile.getMailboxId());

                String where = UserProfileDatabaseOpenHelper.COLUMN_USER_ID + "=?";
                String[] whereArgs = {userProfile.getUserId()};
                return database.update(UserProfileDatabaseOpenHelper.TABLE_USER_PROFILE, values, where, whereArgs);
            } else {
                throw new SQLiteException("No writable database found");
            }
        }
    }

    public boolean isProfileFound(String randomId) {
        SQLiteDatabase database = userProfileDatabaseOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            if (database != null && database.isOpen()) {

                String[] columns = {UserProfileDatabaseOpenHelper.COLUMN_CUSTOMER_NAME,
                        UserProfileDatabaseOpenHelper.COLUMN_BACKGROUND_IMAGE_ID,
                        UserProfileDatabaseOpenHelper.COLUMN_LAST_LOGIN_TIMESTAMP,
                        UserProfileDatabaseOpenHelper.COLUMN_IMAGE_NAME,
                        UserProfileDatabaseOpenHelper.COLUMN_USER_ID
                };

                String where = UserProfileDatabaseOpenHelper.COLUMN_USER_ID + "=?";
                String[] whereArgs = {randomId};
                String groupBy = null;
                String having = null;
                String orderBy = null;

                cursor = database.query(UserProfileDatabaseOpenHelper.TABLE_USER_PROFILE, columns, where, whereArgs, groupBy, having, orderBy);
                return cursor != null && cursor.moveToFirst();
            } else {
                throw new SQLiteException("No readable database found");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (database != null) {
                database.close();
            }
        }
    }

    public synchronized List<UserProfile> loadAllUserProfiles() {
        SQLiteDatabase database = userProfileDatabaseOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            if (database != null && database.isOpen()) {
                List<UserProfile> userProfiles = new ArrayList<>();
                String[] columns = null;
                String where = null;
                String[] whereArgs = null;
                String groupBy = null;
                String having = null;
                String orderBy = UserProfileDatabaseOpenHelper.COLUMN_LAST_LOGIN_TIMESTAMP + " DESC";

                cursor = database.query(UserProfileDatabaseOpenHelper.TABLE_USER_PROFILE, columns, where, whereArgs, groupBy, having, orderBy);
                if (cursor == null) {
                    throw new SQLiteException("No records found...");
                }

                int customerNameColumnIndex = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_CUSTOMER_NAME);
                int backgroundImageIdColumnIndex = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_BACKGROUND_IMAGE_ID);
                int lastLoginTimestampColumnIndex = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_LAST_LOGIN_TIMESTAMP);
                int imageNameColumnIndex = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_IMAGE_NAME);
                int userIdColumnIndex = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_USER_ID);
                int clientTypeColumnIndex = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_CLIENT_TYPE);
                int twoFaEnabledColumnIndex = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_SURE_CHECK_2_ENABLED);
                int languageCodeColumnIndex = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_LANGUAGE_CODE);
                int mailboxIdColumnIndex = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_MAILBOX_ID);
                int migrationVersionIndex = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_MIGRATION_VERSION);
                int columnUserNumber = cursor.getColumnIndex(UserProfileDatabaseOpenHelper.COLUMN_USER_NUMBER);

                while (cursor.moveToNext()) {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setCustomerName(cursor.getString(customerNameColumnIndex));
                    userProfile.setDateTimestamp(cursor.getString(lastLoginTimestampColumnIndex));
                    userProfile.setImageName(cursor.getString(imageNameColumnIndex));
                    userProfile.setUserId(cursor.getString(userIdColumnIndex));
                    userProfile.setClientType(cursor.getString(clientTypeColumnIndex));
                    userProfile.setTwoFAEnabled(cursor.getInt(twoFaEnabledColumnIndex) == 1);
                    userProfile.setUserNumber(cursor.getInt(columnUserNumber));
                    if (cursor.getString(languageCodeColumnIndex) != null) {
                        userProfile.setLanguageCode(cursor.getString(languageCodeColumnIndex));
                    }
                    if (cursor.getString(mailboxIdColumnIndex) != null) {
                        userProfile.setMailboxId(cursor.getString(mailboxIdColumnIndex));
                    }
                    userProfile.setMigrationVersion(cursor.getInt(migrationVersionIndex));
                    userProfiles.add(userProfile);
                }
                return userProfiles;

            } else {
                throw new SQLiteException("No readable database found");
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }

            if (database != null) {
                database.close();
            }
        }
    }
}