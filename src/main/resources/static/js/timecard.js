"use strict";

{
    /**
     * モーダルへの値の受け渡し
     */
    $('#modalForm').on('show.bs.modal', function (event) {
        const button = $(event.relatedTarget);
        const id = button.data('id');
        const date = button.data('date');
        const dateView = button.data('dateView');
        const weekDay = button.data('weekDay');
        const remarks = button.data('remarks');
        const text = dateView + "(" + weekDay + ")";
        // startTime取得
        const customStart = button.data('custom') + "start";
        const startValue = $('#' + customStart).data('id');
        const customDefaultStart = button.data('custom') + "defaultStart";
        const defaultStart = $('#' + customDefaultStart).val();
        let startTime = 0;
        if (startValue != null) {
            startTime = startValue;
        } else {
            startTime = defaultStart;
        }
        // endTime取得
        const customEnd = button.data('custom') + "end";
        const endValue = $('#' + customEnd).data('id');
        const customDefaultEnd = button.data('custom') + "defaultEnd";
        const defaultEnd = $('#' + customDefaultEnd).val();
        let endTime = 0;
        if (endValue != null) {
            endTime = endValue;
        } else {
            endTime = defaultEnd;
        }
        // breakTime取得
        const customBreak = button.data('custom') + "break";
        const breakValue = $('#' + customBreak).data('id');
        const customDefaultBreak = button.data('custom') + "defaultBreak";
        const defaultBreak = $('#' + customDefaultBreak).val();
        let breakTime = 0;
        if (breakValue != null) {
            breakTime = breakValue;
        } else {
            breakTime = defaultBreak;
        }

        // モーダルに値をセット
        const modal = $(this);
        modal.find('#id').val(id);
        modal.find('#dutyDate').val(date);
        modal.find('#dutyDateView').val(text);
        modal.find('#startTime').val(startTime);
        modal.find('#endTime').val(endTime);
        modal.find('#breakTime').val(breakTime);
        modal.find('#remarks').val(remarks);
    })

    /**
     * デフォルトタイム変更
     */
    $(function () {
        $('#defaultTimeButton').click(
            function () {
                // 休憩時間が異常の場合、処理を中断
                if (!checkBreakTime(true)) {
                    return false;
                }

                $("#defaultTimeButton").prop("disabled", true);
                // url
                const hostUrl = '/user/default';
                // headers
                const token = $('#csrf').val();
                // data
                const formData = $('#defaultTimeForm').serializeArray();
                const data = parseJson(formData);

                $("#defaultTimeButton").prepend('<span class="spinner-border spinner-border-sm position-absolute" id="buttonLoad"></span>');

                // ajax通信でPOST処理
                $.ajax({
                    url: hostUrl,
                    type: 'POST',
                    headers: {
                        'X-CSRF-Token': token
                    },
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    dataType: "json",
                    timeout: 3000,
                    // 通信成功時
                }).done(function (data) {
                    // バリデーション通過時
                    if (data["resultFlag"]) {
                        const afterDefaultStart = $('#defaultStartTime').val();
                        const startInputs = $('input[name="startTime"]');
                        startInputs.val(afterDefaultStart);
                        const afterDefaultEnd = $('#defaultEndTime').val();
                        const endInputs = $('input[name="endTime"]');
                        endInputs.val(afterDefaultEnd);
                        const afterDefaultBreak = $('#defaultBreakTime').val();
                        const breakInputs = $('input[name="breakTime"]');
                        breakInputs.val(afterDefaultBreak);
                        // バリデーションエラー時
                    } else {
                        alert(data["message"]);
                    }
                    // 通信失敗時
                }).fail(function () {
                    alert("通信に失敗しました。");
                    // 共通処理
                }).always(() => {
                    setTimeout(function () {
                        $("#buttonLoad").remove();
                        $("#defaultTimeButton").prop("disabled", false);
                    }, 500);
                });
            });
    });


    /**
     * 勤務表データ登録
     */
    $(function () {
        $('.registerButton').click(
            function () {

                // 休憩時間が異常の場合、処理を中断
                if (!checkBreakTime(false)) {
                    return false;
                }

                // 操作無効用id
                const lockClass = "lockClass";

                // 画面操作を無効する
                lockScreen(lockClass);

                // url
                const hostUrl = '/save';
                // headers
                const token = $('#csrf').val();
                // data
                const formId = '#' + $(this).data('id');
                const formData = $(formId).serializeArray();
                const data = parseJson(formData);

                // ajax通信でPOST処理
                $.ajax({
                    url: hostUrl,
                    type: 'POST',
                    headers: {
                        'X-CSRF-Token': token
                    },
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    dataType: "json",
                    timeout: 3000,
                    // 通信成功時
                }).done(function (data) {
                    if (data["resultFlag"]) {
                        location.reload();
                        // バリデーションエラー時
                    } else {
                        alert(data["message"]);
                        unlockScreen(lockClass);
                    }
                }).fail(function () {
                    alert("通信に失敗しました。");
                    unlockScreen(lockClass);
                });
            });

    });

    $(function () {
        $('.deleteButton').click(
            function () {
                // 操作無効用id
                const lockClass = "lockClass";

                // 画面操作を無効する
                lockScreen(lockClass);

                // url
                const hostUrl = '/delete';
                // headers
                const token = $('#csrf').val();
                // data
                const formId = '#' + $(this).data('id');
                const formData = $(formId).serializeArray();
                const data = parseJson(formData);

                // ajax通信でPOST処理
                $.ajax({
                    url: hostUrl,
                    type: 'POST',
                    headers: {
                        'X-CSRF-Token': token
                    },
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    timeout: 3000,
                    // 通信成功時
                }).done(function () {
                    location.reload();
                }).fail(function () {
                    alert("通信に失敗しました。");
                    unlockScreen(lockClass);
                })
            });
    });

    // 出勤退勤時間管理
    $(function ($) {
        // 出勤時間
        $('#defaultStartTime').change(function () {
            onTimeChange(true, true);
            checkBreakTime(true);
        });
        $('#startTime').change(function () {
            onTimeChange(false, true);
            checkBreakTime(false);
        });

        // 退勤時間
        $('#defaultEndTime').change(function () {
            onTimeChange(true, false);
            checkBreakTime(true);
        });
        $('#endTime').change(function () {
            onTimeChange(false, false);
            checkBreakTime(false);
        });

        // 休憩時間
        $('#defaultBreakTime').change(function () {
            checkBreakTime(true);
        });
        $('#breakTime').change(function () {
            checkBreakTime(false);
        });
    });

    const checkBreakTime = function (checkDefault) {
        const startTime = checkDefault ? $('#defaultStartTime') : $('#startTime');
        const endTime = checkDefault ? $('#defaultEndTime') : $('#endTime');
        const breakTime = checkDefault ? $('#defaultBreakTime') : $('#breakTime');
        let checkResult = true;
        if ((endTime.val() - startTime.val()) <= breakTime.val()) {
            alert('休憩時間を再入力してください。');
            breakTime.prop('selectedIndex', 0);
            checkResult = false;
        }
        return checkResult;
    };

    // checkDefault = default 付けるかどうか
    // isStart = endTime(false) 側か startTime(true) 側かで変更するプルダウンを決める
    const onTimeChange = function (checkDefault, isStart) {
        const startTime = checkDefault ? $('#defaultStartTime') : $('#startTime');
        const endTime = checkDefault ? $('#defaultEndTime') : $('#endTime');
        if (startTime.prop('selectedIndex') >= endTime.prop('selectedIndex')) {
            if (isStart) {
                endTime.prop('selectedIndex', startTime.prop('selectedIndex'));
            } else {
                startTime.prop('selectedIndex', endTime.prop('selectedIndex'));
            }
        }
    }

    // const xmls = new XMLHttpRequest();

    // xmls.open('POST', 送信先);
    // xmls.setRequestHeader('content-type', 'application/x-www-form-urlencoded;charset=UTF-8');
    // xmls.send('param="haha"');

    // xmls.onreadystatechange = function () {
    //     if (xmls.readyState === 4 && xmls.status === 200) {
    //         //エラーを出さずに通信が完了した時の処理。例↓
    //         console.log(xmls.responseText);
    //     }
    // }

    // formデータをjson形式に一括変換する関数
    const parseJson = function (data) {
        const returnJson = {};
        for (let i = 0; i < data.length; i++) {
            returnJson[data[i].name] = data[i].value;
        }
        return returnJson;
    }

    // 画面操作無効にする関数
    function lockScreen(className) {

        // 現在画面を覆い隠すためのDIVタグを作成する
        const divTag = $('<div>').attr("class", className);
        const div = $('<div>').attr("class", className);

        // クラスを設定
        divTag.addClass("loading_mask");
        div.addClass("spinner-border loading_mask_inner");

        // BODYタグに作成したDIVタグを追加
        $('body').append(divTag);
        $('body').append(div);
    }

    // 画面操作無効を解除する関数
    function unlockScreen(className) {

        // 画面を覆っているタグを削除する
        $("." + className).remove();
    }
}