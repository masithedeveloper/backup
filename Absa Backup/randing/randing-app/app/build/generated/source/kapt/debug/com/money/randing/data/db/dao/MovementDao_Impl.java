package com.money.randing.data.db.dao;

import android.database.Cursor;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.money.randing.data.db.converter.MovementTypeConverter;
import com.money.randing.data.db.entities.DBMovement;
import com.money.randing.data.model.MovementTypeDto;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class MovementDao_Impl implements MovementDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DBMovement> __insertionAdapterOfDBMovement;

  private final MovementTypeConverter __movementTypeConverter = new MovementTypeConverter();

  private final EntityDeletionOrUpdateAdapter<DBMovement> __deletionAdapterOfDBMovement;

  private final EntityDeletionOrUpdateAdapter<DBMovement> __updateAdapterOfDBMovement;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllPersonMovements;

  public MovementDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDBMovement = new EntityInsertionAdapter<DBMovement>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `movements` (`person_id`,`amount`,`date`,`description`,`type`,`id`) VALUES (?,?,?,?,?,nullif(?, 0))";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DBMovement value) {
        stmt.bindLong(1, value.getPersonId());
        stmt.bindDouble(2, value.getAmount());
        stmt.bindLong(3, value.getDate());
        if (value.getDescription() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDescription());
        }
        final int _tmp;
        _tmp = __movementTypeConverter.fromMovementType(value.getType());
        stmt.bindLong(5, _tmp);
        stmt.bindLong(6, value.getId());
      }
    };
    this.__deletionAdapterOfDBMovement = new EntityDeletionOrUpdateAdapter<DBMovement>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `movements` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DBMovement value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfDBMovement = new EntityDeletionOrUpdateAdapter<DBMovement>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `movements` SET `person_id` = ?,`amount` = ?,`date` = ?,`description` = ?,`type` = ?,`id` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DBMovement value) {
        stmt.bindLong(1, value.getPersonId());
        stmt.bindDouble(2, value.getAmount());
        stmt.bindLong(3, value.getDate());
        if (value.getDescription() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDescription());
        }
        final int _tmp;
        _tmp = __movementTypeConverter.fromMovementType(value.getType());
        stmt.bindLong(5, _tmp);
        stmt.bindLong(6, value.getId());
        stmt.bindLong(7, value.getId());
      }
    };
    this.__preparedStmtOfDeleteAllPersonMovements = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM movements WHERE person_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertMovement(final DBMovement value, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDBMovement.insert(value);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object deleteMovement(final DBMovement value, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDBMovement.handle(value);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object updateMovement(final DBMovement value, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDBMovement.handle(value);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object deleteAllPersonMovements(final int personId,
      final Continuation<? super Integer> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllPersonMovements.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, personId);
        __db.beginTransaction();
        try {
          final java.lang.Integer _result = _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteAllPersonMovements.release(_stmt);
        }
      }
    }, p1);
  }

  @Override
  public Flow<List<DBMovement>> getAll() {
    final String _sql = "SELECT * FROM movements";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"movements"}, new Callable<List<DBMovement>>() {
      @Override
      public List<DBMovement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPersonId = CursorUtil.getColumnIndexOrThrow(_cursor, "person_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final List<DBMovement> _result = new ArrayList<DBMovement>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DBMovement _item;
            final int _tmpPersonId;
            _tmpPersonId = _cursor.getInt(_cursorIndexOfPersonId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final MovementTypeDto _tmpType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfType);
            _tmpType = __movementTypeConverter.toMovementType(_tmp);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item = new DBMovement(_tmpPersonId,_tmpAmount,_tmpDate,_tmpDescription,_tmpType,_tmpId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DBMovement>> getAllSortedByDate() {
    final String _sql = "SELECT * FROM movements ORDER BY date ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"movements"}, new Callable<List<DBMovement>>() {
      @Override
      public List<DBMovement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPersonId = CursorUtil.getColumnIndexOrThrow(_cursor, "person_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final List<DBMovement> _result = new ArrayList<DBMovement>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DBMovement _item;
            final int _tmpPersonId;
            _tmpPersonId = _cursor.getInt(_cursorIndexOfPersonId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final MovementTypeDto _tmpType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfType);
            _tmpType = __movementTypeConverter.toMovementType(_tmp);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item = new DBMovement(_tmpPersonId,_tmpAmount,_tmpDate,_tmpDescription,_tmpType,_tmpId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DBMovement>> getPersonMovements(final int personId) {
    final String _sql = "SELECT * FROM movements WHERE person_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, personId);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"movements"}, new Callable<List<DBMovement>>() {
      @Override
      public List<DBMovement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPersonId = CursorUtil.getColumnIndexOrThrow(_cursor, "person_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final List<DBMovement> _result = new ArrayList<DBMovement>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DBMovement _item;
            final int _tmpPersonId;
            _tmpPersonId = _cursor.getInt(_cursorIndexOfPersonId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final MovementTypeDto _tmpType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfType);
            _tmpType = __movementTypeConverter.toMovementType(_tmp);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item = new DBMovement(_tmpPersonId,_tmpAmount,_tmpDate,_tmpDescription,_tmpType,_tmpId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DBMovement>> getPersonMovementsSortedByDate(final int personId) {
    final String _sql = "SELECT * FROM movements WHERE person_id = ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, personId);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"movements"}, new Callable<List<DBMovement>>() {
      @Override
      public List<DBMovement> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPersonId = CursorUtil.getColumnIndexOrThrow(_cursor, "person_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final List<DBMovement> _result = new ArrayList<DBMovement>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DBMovement _item;
            final int _tmpPersonId;
            _tmpPersonId = _cursor.getInt(_cursorIndexOfPersonId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final MovementTypeDto _tmpType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfType);
            _tmpType = __movementTypeConverter.toMovementType(_tmp);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item = new DBMovement(_tmpPersonId,_tmpAmount,_tmpDate,_tmpDescription,_tmpType,_tmpId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Double> getPersonTotal(final int personId) {
    final String _sql = "SELECT SUM(amount) FROM movements WHERE person_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, personId);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"movements"}, new Callable<Double>() {
      @Override
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if(_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getMovement(final int id, final Continuation<? super DBMovement> p1) {
    final String _sql = "SELECT * FROM movements WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.execute(__db, false, new Callable<DBMovement>() {
      @Override
      public DBMovement call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPersonId = CursorUtil.getColumnIndexOrThrow(_cursor, "person_id");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final DBMovement _result;
          if(_cursor.moveToFirst()) {
            final int _tmpPersonId;
            _tmpPersonId = _cursor.getInt(_cursorIndexOfPersonId);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final MovementTypeDto _tmpType;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfType);
            _tmpType = __movementTypeConverter.toMovementType(_tmp);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _result = new DBMovement(_tmpPersonId,_tmpAmount,_tmpDate,_tmpDescription,_tmpType,_tmpId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, p1);
  }
}
