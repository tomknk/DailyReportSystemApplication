package com.techacademy.constants;

// エラーメッセージ定義
public enum ErrorKinds {

    // エラー内容
    // 空白チェックエラー
    BLANK_ERROR,
    // 半角英数字チェックエラー
    HALFSIZE_ERROR,
    // 桁数(8桁~16桁以外)チェックエラー
    RANGECHECK_ERROR,
    // 重複チェックエラー(例外あり)
    DUPLICATE_EXCEPTION_ERROR,
    // 重複チェックエラー(例外なし)
    DUPLICATE_ERROR,
    // ログイン中削除チェックエラー
    LOGINCHECK_ERROR,
    // 日付チェックエラー
    DATECHECK_ERROR,
    // チェックOK
    CHECK_OK,
    // 正常終了
    SUCCESS,
    //従業員が存在しない 追加
    NOT_FOUND_ERROR,
    //エラー発生　追加
    UPDATE_ERROR,
    // 入力値が空であるエラー 追加
    VALUE_BLANK_ERROR,
    // 入力値が100文字を超えているエラー 追加
    VALUE_EXCEED_100_ERROR,
    // 入力値が600文字を超えているエラー 追加
    VALUE_EXCEED_600_ERROR
}
