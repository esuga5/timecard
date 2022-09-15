package com.vvenn.timecard.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Optional;
import java.util.List;

import com.vvenn.timecard.entity.User;
import com.vvenn.timecard.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * コントローラーから受け取った値を元に、DBにデータを登録します。
     *
     * @param username     メールアドレス
     * @param password     パスワード
     * @param displayName  氏名
     * @param sectionCode  所属部門コード
     * @param joinedAt     入社日
     * @param adminFlag    管理者権限の有無
     * @param inactiveFlag ユーザー削除フラグの有無
     * @return user ユーザーデータ
     * @throws ParseException SimpleDateFormat実行時エラー
     */
    public User registerUser(String username, String password, String displayName, String sectionCode, String joinedAt,
            boolean adminFlag, boolean inactiveFlag) throws ParseException {

        // 現在時を取得
        Timestamp now = Helper.getNowTimestamp();

        // ユーザー登録処理
        User user = new User(null, username, this.passwordEncoder.encode(password), displayName, sectionCode,
                Helper.dateStringToTimestamp(joinedAt), "", 10 * 60, 19 * 60, 1 * 60, adminFlag, inactiveFlag, now, now,
                null);
        this.repository.save(user);

        return user;

    }

    /**
     * コントローラーから受け取った値を元に、ユーザー情報を変更します。
     *
     * @param user        対象ユーザーのid
     * @param username    メールアドレス
     * @param password    パスワード
     * @param displayName 氏名
     * @param sectionCode 所属部門コード
     * @param joinedAt    入社日
     * @param adminFlag   管理者権限の有無
     * @return user ユーザーデータ
     * @throws ParseException SimpleDateFormat実行時エラー
     */
    public User updateUser(User user, String username, String password, String displayName, String sectionCode,
            String joinedAt, boolean adminFlag) throws ParseException {

        // パスワードエンコードの有無を設定
        String updatePassword = null;
        if (password.equals(user.getPassword())) {
            updatePassword = user.getPassword();
        } else {
            updatePassword = this.passwordEncoder.encode(password);
        }

        user.setJoinedAt(Helper.dateStringToTimestamp(joinedAt));
        user.setDisplayName(displayName);
        user.setUsername(username);
        user.setPassword(updatePassword);
        user.setSectionCode(sectionCode);
        user.setAdminFlag(adminFlag);
        user.setUpdatedAt(Helper.getNowTimestamp());

        this.repository.save(user);

        return user;

    }

    // ユーザーデフォルトタイム変更用

    /**
     * usersテーブルの対象レコードのデフォルト出勤、退勤休憩時間を変更します。
     * 
     * @param user             処理対象ユーザー
     * @param defaultStartTime デフォルト出勤時間
     * @param defaultEndTime   デフォルト退勤時間
     * @param defaultBreakTime デフォルト休憩時間
     * @return 変更後のユーザーエンティティ
     */
    public User updateDefaultTime(User user, int defaultStartTime, int defaultEndTime, int defaultBreakTime) {

        user.setDefaultStartTime(defaultStartTime);
        user.setDefaultEndTime(defaultEndTime);
        user.setDefaultBreakTime(defaultBreakTime);

        this.repository.save(user);

        return user;
    }

    /**
     * 渡された値を元に、DBから対象ユーザーの情報を取得します。
     *
     * @param id 検索対象のユーザーid
     * @return 存在する場合: data.get(), 存在しない場合: null
     */
    public User getData(Long id) {

        Optional<User> data = this.repository.findById(id);

        return data.isPresent() ? data.get() : null;

    }

    /**
     * 渡された値を元に、パスワードの変更及び更新日時を変更します。
     *
     * @param user     ログイン中のユーザー情報
     * @param password パスワード
     */
    public void updatePassword(User user, String password) {

        user.setPassword(this.passwordEncoder.encode(password));
        user.setUpdatedAt(Helper.getNowTimestamp());

        this.repository.save(user);

    }

    /**
     * DBに登録されたユーザーをusernameで取得します。
     *
     * @param username
     * @return DBに登録されたテストユーザー
     */
    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || "".contentEquals(username)) {
            throw new UsernameNotFoundException("Username is empty");
        }
        User user = this.repository.findByUsername(username);
        if (user == null || user.isInactiveFlag() == true) {
            throw new UsernameNotFoundException("User not found:" + username);
        }
        return user;
    }

    /**
     * usersテーブルのレコードを全件取得します。
     * 
     * @return List<User>
     */
    public List<User> findUserAll() {
        return this.repository.findAll();
    }

    /**
     * usersテーブルのアクティブなレコードを全件取得します。
     * 
     * @return アクティブなユーザーリスト
     */
    public List<User> findActiveUserAll() {
        return this.repository.findActiveUserAll();
    }

    /**
     * usersテーブルのアクティブなレコードを全件取得します。<br>
     * DIされたページャによってページング可能とします。
     * 
     * @param pageable
     * @return User型のPage
     */
    public Page<User> findActiveUserAll(Pageable pageable) {
        return this.repository.findActiveUserAll(pageable);
    }

    /**
     * usersテーブルのレコードを論理削除します。
     * 
     * @param id 論理削除の対象id
     */
    public void softDeleteUserById(Long id) {
        User user = this.repository.findById(id).get();
        user.setInactiveFlag(true);
        user.setUpdatedAt(Helper.getNowTimestamp());
        this.repository.save(user);
    }
}
