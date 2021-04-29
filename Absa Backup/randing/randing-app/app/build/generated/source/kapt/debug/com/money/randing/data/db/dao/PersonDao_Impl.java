package com.money.randing.data.db.dao;

import android.database.Cursor;
import android.graphics.Bitmap;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.money.randing.data.db.converter.BitmapConverter;
import com.money.randing.data.db.entities.DBPerson;
import com.money.randing.data.db.entities.DBPersonWithTotal;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
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
public final class PersonDao_Impl implements PersonDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DBPerson> __insertionAdapterOfDBPerson;

  private final BitmapConverter __bitmapConverter = new BitmapConverter();

  private final EntityDeletionOrUpdateAdapter<DBPerson> __updateAdapterOfDBPerson;

  private final SharedSQLiteStatement __preparedStmtOfDeletePerson;

  public PersonDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDBPerson = new EntityInsertionAdapter<DBPerson>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `persons` (`name`,`created_at`,`picture`,`id`) VALUES (?,?,?,nullif(?, 0))";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DBPerson value) {
        if (value.getName() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getName());
        }
        stmt.bindLong(2, value.getCreatedAt());
        final byte[] _tmp;
        _tmp = __bitmapConverter.fromBitmap(value.getPicture());
        if (_tmp == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindBlob(3, _tmp);
        }
        stmt.bindLong(4, value.getId());
      }
    };
    this.__updateAdapterOfDBPerson = new EntityDeletionOrUpdateAdapter<DBPerson>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `persons` SET `name` = ?,`created_at` = ?,`picture` = ?,`id` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DBPerson value) {
        if (value.getName() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getName());
        }
        stmt.bindLong(2, value.getCreatedAt());
        final byte[] _tmp;
        _tmp = __bitmapConverter.fromBitmap(value.getPicture());
        if (_tmp == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindBlob(3, _tmp);
        }
        stmt.bindLong(4, value.getId());
        stmt.bindLong(5, value.getId());
      }
    };
    this.__preparedStmtOfDeletePerson = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM persons WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertPerson(final DBPerson person, final Continuation<? super Long> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfDBPerson.insertAndReturnId(person);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object updatePerson(final DBPerson person, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDBPerson.handle(person);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public Object deletePerson(final int id, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePerson.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeletePerson.release(_stmt);
        }
      }
    }, p1);
  }

  @Override
  public Flow<List<DBPerson>> getAll() {
    final String _sql = "SELECT * FROM persons ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"persons"}, new Callable<List<DBPerson>>() {
      @Override
      public List<DBPerson> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final List<DBPerson> _result = new ArrayList<DBPerson>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DBPerson _item;
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Bitmap _tmpPicture;
            final byte[] _tmp;
            _tmp = _cursor.getBlob(_cursorIndexOfPicture);
            _tmpPicture = __bitmapConverter.toBitmap(_tmp);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item = new DBPerson(_tmpName,_tmpCreatedAt,_tmpPicture,_tmpId);
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
  public Flow<List<DBPersonWithTotal>> getAllPersonsWithTotal() {
    final String _sql = "SELECT p.id, p.name, p.created_at, p.picture, SUM(m.amount) AS total FROM persons p INNER JOIN movements m ON p.id = m.person_id GROUP BY p.id, p.name, p.created_at, p.picture HAVING total != 0 ORDER BY ABS(total) DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"persons","movements"}, new Callable<List<DBPersonWithTotal>>() {
      @Override
      public List<DBPersonWithTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final List<DBPersonWithTotal> _result = new ArrayList<DBPersonWithTotal>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DBPersonWithTotal _item;
            final Double _tmpTotal;
            if (_cursor.isNull(_cursorIndexOfTotal)) {
              _tmpTotal = null;
            } else {
              _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            }
            final DBPerson _tmpPerson;
            if (! (_cursor.isNull(_cursorIndexOfId) && _cursor.isNull(_cursorIndexOfName) && _cursor.isNull(_cursorIndexOfCreatedAt) && _cursor.isNull(_cursorIndexOfPicture))) {
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final Bitmap _tmpPicture;
              final byte[] _tmp;
              _tmp = _cursor.getBlob(_cursorIndexOfPicture);
              _tmpPicture = __bitmapConverter.toBitmap(_tmp);
              _tmpPerson = new DBPerson(_tmpName,_tmpCreatedAt,_tmpPicture,_tmpId);
            }  else  {
              _tmpPerson = null;
            }
            _item = new DBPersonWithTotal(_tmpPerson,_tmpTotal);
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
  public Object getAllPersonsWithTotalOneTime(
      final Continuation<? super List<DBPersonWithTotal>> p0) {
    final String _sql = "SELECT p.id, p.name, p.created_at, p.picture, SUM(m.amount) AS total FROM persons p INNER JOIN movements m ON p.id = m.person_id GROUP BY p.id, p.name, p.created_at, p.picture HAVING total != 0 ORDER BY ABS(total) DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.execute(__db, false, new Callable<List<DBPersonWithTotal>>() {
      @Override
      public List<DBPersonWithTotal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final List<DBPersonWithTotal> _result = new ArrayList<DBPersonWithTotal>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DBPersonWithTotal _item;
            final Double _tmpTotal;
            if (_cursor.isNull(_cursorIndexOfTotal)) {
              _tmpTotal = null;
            } else {
              _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            }
            final DBPerson _tmpPerson;
            if (! (_cursor.isNull(_cursorIndexOfId) && _cursor.isNull(_cursorIndexOfName) && _cursor.isNull(_cursorIndexOfCreatedAt) && _cursor.isNull(_cursorIndexOfPicture))) {
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final String _tmpName;
              _tmpName = _cursor.getString(_cursorIndexOfName);
              final long _tmpCreatedAt;
              _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
              final Bitmap _tmpPicture;
              final byte[] _tmp;
              _tmp = _cursor.getBlob(_cursorIndexOfPicture);
              _tmpPicture = __bitmapConverter.toBitmap(_tmp);
              _tmpPerson = new DBPerson(_tmpName,_tmpCreatedAt,_tmpPicture,_tmpId);
            }  else  {
              _tmpPerson = null;
            }
            _item = new DBPersonWithTotal(_tmpPerson,_tmpTotal);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, p0);
  }

  @Override
  public Flow<DBPerson> getPerson(final int id) {
    final String _sql = "SELECT * FROM persons WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"persons"}, new Callable<DBPerson>() {
      @Override
      public DBPerson call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final DBPerson _result;
          if(_cursor.moveToFirst()) {
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Bitmap _tmpPicture;
            final byte[] _tmp;
            _tmp = _cursor.getBlob(_cursorIndexOfPicture);
            _tmpPicture = __bitmapConverter.toBitmap(_tmp);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _result = new DBPerson(_tmpName,_tmpCreatedAt,_tmpPicture,_tmpId);
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
  public Object getPersonOneTime(final int id, final Continuation<? super DBPerson> p1) {
    final String _sql = "SELECT * FROM persons WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.execute(__db, false, new Callable<DBPerson>() {
      @Override
      public DBPerson call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final int _cursorIndexOfPicture = CursorUtil.getColumnIndexOrThrow(_cursor, "picture");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final DBPerson _result;
          if(_cursor.moveToFirst()) {
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Bitmap _tmpPicture;
            final byte[] _tmp;
            _tmp = _cursor.getBlob(_cursorIndexOfPicture);
            _tmpPicture = __bitmapConverter.toBitmap(_tmp);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _result = new DBPerson(_tmpName,_tmpCreatedAt,_tmpPicture,_tmpId);
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
