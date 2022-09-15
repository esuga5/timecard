"use strict";

/**
 * 共通スクリプト
 */
{

}

function formConfirm() {
    if (window.confirm('送信してよろしいですか？')) {
        return true;
    } else {
        window.alert('キャンセルされました');
        return false;
    }
}

function deleteByUserId(button) {
    if (confirm('下記のユーザーを削除します。よろしいですか？\n「' + button.dataset.name + '」')) {
        location.href = '/user/delete/' + button.dataset.userId;
    }
}
