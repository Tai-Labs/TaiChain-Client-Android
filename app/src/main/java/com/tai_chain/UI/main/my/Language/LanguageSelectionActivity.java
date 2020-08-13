package com.tai_chain.UI.main.my.Language;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.tai_chain.R;
import com.tai_chain.adapter.LanguageAdapter;
import com.tai_chain.app.ActivityUtils;
import com.tai_chain.app.AppManager;
import com.tai_chain.base.BaseActivity;
import com.tai_chain.bean.languageEntity;
import com.tai_chain.UI.main.MainActivity;
import com.tai_chain.UI.normalvp.NormalPresenter;
import com.tai_chain.UI.normalvp.NormalView;
import com.tai_chain.utils.LocalManageUtil;
import com.tai_chain.utils.SharedPrefsUitls;
import com.gyf.barlibrary.ImmersionBar;

public class LanguageSelectionActivity extends BaseActivity<NormalView, NormalPresenter> implements NormalView {

    private RecyclerView etz_node_lv;
    LanguageAdapter adapter;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_node_selection;
    }

    @Override
    public NormalPresenter initPresenter() {
        return new NormalPresenter();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setCenterTitle(getResources().getString(R.string.my_yysz));
        etz_node_lv = findViewById(R.id.etz_node_lv);
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .init();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        etz_node_lv.setLayoutManager(linearLayoutManager);
        adapter = new LanguageAdapter(R.layout.node_list_item, LocalManageUtil.getLanguageList(this));
        etz_node_lv.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                languageEntity item = (languageEntity) adapter.getItem(position);
                if (item.getLid() == 1) {
                    SharedPrefsUitls.getInstance().putPreferredFiatIso(LanguageSelectionActivity.this, "CNY");
                } else if (item.getLid() == 2) {
                    SharedPrefsUitls.getInstance().putPreferredFiatIso(LanguageSelectionActivity.this, "USD");
                } else if (item.getLid() == 3) {
                    SharedPrefsUitls.getInstance().putPreferredFiatIso(LanguageSelectionActivity.this, "KRW");
                }
                LocalManageUtil.saveSelectLanguage(activity, item.getLid());
                AppManager.getAppManager().finishAllActivity();
                ActivityUtils.next(activity, MainActivity.class);
//                adapter.notifyDataSetChanged();
            }

        });

    }

    @Override
    protected void initData() {

    }

    @Override
    public void initEvent() {
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
