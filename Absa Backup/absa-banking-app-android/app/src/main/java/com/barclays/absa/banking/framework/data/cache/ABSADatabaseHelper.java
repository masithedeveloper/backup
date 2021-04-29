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
package com.barclays.absa.banking.framework.data.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.barclays.absa.banking.boundary.model.AddBeneficiaryObject;
import com.barclays.absa.banking.framework.app.BMBApplication;

/**
 * ABSADatabaseHelper provides access to database.
 */
public class ABSADatabaseHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ABSA";
    /**
     * Beneficiary table name
     */
    private static final String BENEFICIARY_TABLE = "beneficiary_table";

    /**
     * Column beneficiary id
     */
    private static final String COL_BEN_ID = "ben_id";

    /**
     * Column beneficiary type
     */
    private static final String COL_BEN_TYPE = "ben_type";

    /**
     * Column formatted image name
     */
    private static final String COL_FORMATTED_IMAGE_NAME = "formatted_image_name";

    /**
     * Column beneficiary image services
     */
    private static final String COL_BEN_IMAGE_DATA = "ben_image_data";

    /**
     * Column beneficiary image timestamp
     */
    private static final String COL_BEN_IMAGE_TIMESTAMP = "ben_image_timestamp";

    /**
     * SQLite db instance
     */
    private SQLiteDatabase db;

    /**
     * Application context
     */
    private final Context context;

    /**
     * Constructor
     *
     * @param context application context
     */
    ABSADatabaseHelper(Context context) {
        this.context = context;
    }

    public static class ABSADatabase extends SQLiteOpenHelper {

        /**
         * Constructor
         *
         * @param context application context
         */
        ABSADatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + BENEFICIARY_TABLE + " (" + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                    + COL_BEN_ID + " TEXT," + COL_BEN_TYPE + " TEXT," + COL_FORMATTED_IMAGE_NAME
                    + " TEXT UNIQUE," + COL_BEN_IMAGE_DATA + " BLOB, " + COL_BEN_IMAGE_TIMESTAMP + " TEXT"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + BENEFICIARY_TABLE);
            onCreate(db);
        }

    }

    /**
     * Opens database in writable mode
     *
     * @return database instance
     */
    public void open() {
		/*
	  ABSADatabase instance
	 */
        ABSADatabase database = new ABSADatabase(context);
        db = database.getWritableDatabase();
    }

    /**
     * Closes database
     */
    public void close() {
        if (null != db) {
            db.close();
        }
    }

    /**
     * Returns beneficiary record for given beneficiary id
     *
     * @param formattedName image formatted name
     * @return beneficiary record
     * @throws SQLiteException when failed to read record
     */
    AddBeneficiaryObject getImage(String formattedName) throws SQLiteException {
        AddBeneficiaryObject beneficiary = null;

        Cursor cursor = null;
        try {
            cursor = db.query(BENEFICIARY_TABLE, new String[]{COL_BEN_ID, COL_BEN_TYPE,
                            COL_FORMATTED_IMAGE_NAME, COL_BEN_IMAGE_DATA, COL_BEN_IMAGE_TIMESTAMP},
                    COL_FORMATTED_IMAGE_NAME + "=" + "'" + formattedName + "'", null, null, null, COL_BEN_ID);

            if (null != cursor && cursor.moveToFirst()) {
                beneficiary = new AddBeneficiaryObject();
                do {
                    beneficiary.setBeneficiaryId(cursor.getString(cursor.getColumnIndexOrThrow(COL_BEN_ID)));
                    beneficiary.setBeneficiaryType(cursor.getString(cursor.getColumnIndexOrThrow(COL_BEN_TYPE)));
                    beneficiary.setImageName(cursor.getString(cursor
                            .getColumnIndexOrThrow(COL_FORMATTED_IMAGE_NAME)));
                    beneficiary
                            .setImageData(cursor.getBlob(cursor.getColumnIndexOrThrow(COL_BEN_IMAGE_DATA)));
                    beneficiary.setTimestamp(cursor.getString(cursor
                            .getColumnIndexOrThrow(COL_BEN_IMAGE_TIMESTAMP)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            BMBApplication.getInstance().logCaughtException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return beneficiary;
    }

    /**
     * Returns beneficiary record for given beneficiary id
     *
     * @param benId   beneficiary id
     * @param benType beneficiary type
     * @return beneficiary record
     * @throws SQLiteException when failed to read record
     */
    AddBeneficiaryObject getImage(String benId, String benType) throws SQLiteException {
        AddBeneficiaryObject beneficiary = null;

        Cursor cursor = null;
        try {
            cursor = db.query(BENEFICIARY_TABLE, new String[]{COL_FORMATTED_IMAGE_NAME,
                    COL_BEN_IMAGE_DATA, COL_BEN_IMAGE_TIMESTAMP}, COL_FORMATTED_IMAGE_NAME + "=" + "'" + benId
                    + "'" + " AND " + COL_BEN_TYPE + "=" + "'" + benType + "'", null, null, null, COL_BEN_ID);

            if (null != cursor && cursor.moveToFirst()) {
                beneficiary = new AddBeneficiaryObject();
                do {
                    beneficiary.setBeneficiaryId(benId);
                    beneficiary.setBeneficiaryType(benType);
                    beneficiary.setImageName(cursor.getString(cursor
                            .getColumnIndexOrThrow(COL_FORMATTED_IMAGE_NAME)));
                    beneficiary
                            .setImageData(cursor.getBlob(cursor.getColumnIndexOrThrow(COL_BEN_IMAGE_DATA)));
                    beneficiary.setTimestamp(cursor.getString(cursor
                            .getColumnIndexOrThrow(COL_BEN_IMAGE_TIMESTAMP)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            BMBApplication.getInstance().logCaughtException(e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return beneficiary;
    }

    AddBeneficiaryObject getImageFromId(String benId, String benType) throws SQLiteException {
        AddBeneficiaryObject beneficiary = null;

        Cursor cursor = null;
        try {
            cursor = db.query(BENEFICIARY_TABLE, new String[]{COL_FORMATTED_IMAGE_NAME,
                    COL_BEN_IMAGE_DATA, COL_BEN_IMAGE_TIMESTAMP}, COL_BEN_ID + "=" + "'" + benId
                    + "'" + " AND " + COL_BEN_TYPE + "=" + "'" + benType + "'", null, null, null, COL_BEN_ID);

            if (null != cursor && cursor.moveToFirst()) {
                beneficiary = new AddBeneficiaryObject();
                do {
                    beneficiary.setBeneficiaryId(benId);
                    beneficiary.setBeneficiaryType(benType);
                    beneficiary.setImageName(cursor.getString(cursor
                            .getColumnIndexOrThrow(COL_FORMATTED_IMAGE_NAME)));
                    beneficiary
                            .setImageData(cursor.getBlob(cursor.getColumnIndexOrThrow(COL_BEN_IMAGE_DATA)));
                    beneficiary.setTimestamp(cursor.getString(cursor
                            .getColumnIndexOrThrow(COL_BEN_IMAGE_TIMESTAMP)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            BMBApplication.getInstance().logCaughtException(e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return beneficiary;
    }

    /**
     * Insert beneficiary record
     *
     * @param beneficiary beneficiary record
     * @return row id or -1 if row not inserted
     */
    Long insertBeneficiary(AddBeneficiaryObject beneficiary) throws SQLiteException {
        if (null != beneficiary) {
            ContentValues cv = new ContentValues();
            cv.put(COL_BEN_ID, beneficiary.getBeneficiaryId());
            cv.put(COL_BEN_TYPE, beneficiary.getBeneficiaryType());
            cv.put(COL_FORMATTED_IMAGE_NAME, beneficiary.getImageName());
            cv.put(COL_BEN_IMAGE_TIMESTAMP, beneficiary.getTimestamp());
            cv.put(COL_BEN_IMAGE_DATA, beneficiary.getImageData());
            return db.replace(BENEFICIARY_TABLE, null, cv);
        }
        return -1L;
    }

    /**
     * Updates beneficiary record for given image name
     *
     * @param beneficiary beneficiary record
     * @return number of rows updated
     */
    Integer updateBeneficiary(AddBeneficiaryObject beneficiary) {
        String formattedName;
        String timestamp;
        byte[] imageData;

        if (null != beneficiary) {
            ContentValues cv = new ContentValues();
            if (null != (formattedName = beneficiary.getImageName())) {
                cv.put(COL_FORMATTED_IMAGE_NAME, formattedName);
            }
            if (null != (timestamp = beneficiary.getTimestamp())) {
                cv.put(COL_BEN_IMAGE_TIMESTAMP, timestamp);
            }
            if (null != (imageData = beneficiary.getImageData())) {
                cv.put(COL_BEN_IMAGE_DATA, imageData);
            }

            return db.update(BENEFICIARY_TABLE, cv,
                    COL_FORMATTED_IMAGE_NAME + "=" + "'" + beneficiary.getImageName() + "'", null);
        }
        return -1;
    }

    /**
     * Deletes beneficiary record for given beneficiary id
     *
     * @param benId   beneficiary id
     * @param benType beneficiary type
     * @return number of rows deleted
     */
    Integer deleteBeneficiary(String benId, String benType) {
        if (null != benId) {
            return db.delete(BENEFICIARY_TABLE, COL_FORMATTED_IMAGE_NAME + "=" + "'" + benId + "'"
                    + " AND " + COL_BEN_TYPE + " LIKE " + "'" + benType + "'", null);
        }
        return -1;
    }

    Integer deleteBeneficiaryFromId(String benId, String benType) {
        if (null != benId) {
            return db.delete(BENEFICIARY_TABLE, COL_BEN_ID + "=" + "'" + benId + "'"
                    + " AND " + COL_BEN_TYPE + " LIKE " + "'" + benType + "'", null);
        }
        return -1;
    }

}