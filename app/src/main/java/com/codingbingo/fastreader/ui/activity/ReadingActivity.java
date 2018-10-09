package com.codingbingo.fastreader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.codingbingo.fastreader.Constants;
import com.codingbingo.fastreader.R;
import com.codingbingo.fastreader.base.BaseActivity;
import com.codingbingo.fastreader.dao.Book;
import com.codingbingo.fastreader.dao.BookDao;
import com.codingbingo.fastreader.ui.fragment.ChapterListFragment;
import com.codingbingo.fastreader.ui.fragment.ReadingFragment;

import java.util.List;


/**
 * Author: bingo
 * Email: codingbingo@gmail.com
 * By 2017/1/11.
 */

public class ReadingActivity extends BaseActivity implements View.OnClickListener {
    private ChapterListFragment mChapterListFragment;
    private ReadingFragment mReadingFragment;

    private long bookId = NO_BOOK_ID;
    private String bookPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //进入activity，先进入全屏状态
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_reading);


        init();
        initView();
    }

    private void init() {
        Intent intent = getIntent();
        if (intent.hasExtra("type") == false) {
            finish();
            return;
        }

        switch (intent.getIntExtra("type", Constants.TYPE_FROM_MAIN_ACTIVITY)) {
            case Constants.TYPE_FROM_MAIN_ACTIVITY:
                //从主页面进来的，说明本地数据已经插入数据库了
                bookId = intent.getLongExtra("bookId", 0);
                break;
            case Constants.TYPE_FROM_LOCAL_FILE_ACTIVITY:
                bookPath = intent.getStringExtra("bookPath");
                //防止用户再从文件列表页点击进入
                List<Book> bookList = getDaoSession().getBookDao().queryBuilder().where(BookDao.Properties.BookPath.eq(bookPath)).list();
                if (bookList.size() != 0) {
                    bookId = bookList.get(0).getId();
                    break;
                }
        }
    }

    private void initView() {
        mReadingFragment = new ReadingFragment();
        mReadingFragment.setBookId(bookId);
        mReadingFragment.setBookPath(bookPath);
        mReadingFragment.setOnClickListener(this);

        mChapterListFragment = new ChapterListFragment();
        mChapterListFragment.setBookId(bookId);
        mChapterListFragment.setBookPath(bookPath);

        if (mReadingFragment.isAdded()){
            getFragmentManager()
                    .beginTransaction()
                    .show(mReadingFragment)
                    .commitAllowingStateLoss();
        } else{
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.reading_container, mReadingFragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.backBtn:
                finish();
                break;
            case R.id.book_contents:
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.reading_container, mChapterListFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mReadingFragment.isHidden() == false) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                mReadingFragment.nextPage();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                mReadingFragment.prePage();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
