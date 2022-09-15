DROP TABLE IF EXISTS time_card_records;

CREATE TABLE time_card_records (
    id SERIAL NOT NULL,
    -- 勤務表シリアル
    user_id INTEGER NOT NULL,
    -- ユーザーシリアル
    duty_date date NOT NULL,
    --勤務日付
    start_time INTEGER NOT NULL,
    --出勤時間
    end_time INTEGER NOT NULL,
    --退勤時間
    break_time INTEGER NOT NULL,
    --休憩時間
    remarks varchar(40),
    --備考
    created_at timestamp NOT NULL,
    --登録日時
    updated_at timestamp NOT NULL,
    --更新日時
    over_time INTEGER NOT NULL,
    --残業判定
    PRIMARY KEY (id)
);

COMMENT ON COLUMN
    time_card_records.id IS '勤務表シリアル';
COMMENT ON COLUMN
    time_card_records.user_id IS 'ユーザーシリアル';
COMMENT ON COLUMN
    time_card_records.duty_date IS '勤務日付';
COMMENT ON COLUMN
    time_card_records.start_time IS '出勤時間';
COMMENT ON COLUMN
    time_card_records.end_time IS '退勤時間';
COMMENT ON COLUMN
    time_card_records.break_time IS '休憩時間';
COMMENT ON COLUMN
    time_card_records.remarks IS '備考';
COMMENT ON COLUMN
    time_card_records.created_at IS '登録日時';
COMMENT ON COLUMN
    time_card_records.updated_at IS '更新日時';
COMMENT ON COLUMN
    time_card_records.over_time IS '残業判定';

-- test用勤務表データ登録
INSERT INTO
    time_card_records (user_id, duty_date, start_time, end_time, break_time, remarks, created_at, updated_at, over_time)
VALUES
    (1, '2020/05/11', 600, 1140, 60, 'test 5月', '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610', '0')
    , (1, '2020/05/12', 630, 1140, 60, 'test 5月 リモート', '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610', '0')
    , (1, '2020/06/11', 600, 1140, 60, 'test 6月', '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610', '0')
    , (1, '2020/06/12', 630, 1140, 60, 'test 6月 リモート', '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610', '0')
    , (1, '2020/06/14', 600, 1140, 60, 'test 6月 リモート', '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610', '0')
    , (2, '2020/05/11', 600, 1140, 60, 'test 5月 admin', '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610', '0')
    , (2, '2020/05/12', 630, 1140, 60, 'test 5月 admin リモート', '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610', '0')
    , (2, '2020/06/11', 600, 1140, 60, 'test 6月 admin', '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610', '0')
    , (2, '2020/06/12', 630, 1140, 60, 'test 6月 admin リモート', '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610', '0')
    , (2, '2020/06/14', 600, 1140, 60, 'test 6月 admin リモート', '2020/05/08 14:48:52.610', '2020/05/08 14:48:52.610', '0')
;