package com.paullouis.travel.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.card.MaterialCardView;
import com.paullouis.travel.R;

public class StatCardView extends MaterialCardView {

    private ImageView ivIcon;
    private TextView tvValue;
    private TextView tvTitle;

    public StatCardView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public StatCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StatCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_stat_card, this, true);
        ivIcon = findViewById(R.id.ivStatIcon);
        tvValue = findViewById(R.id.tvStatValue);
        tvTitle = findViewById(R.id.tvStatTitle);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatCardView);
            String title = a.getString(R.styleable.StatCardView_statTitle);
            String value = a.getString(R.styleable.StatCardView_statValue);
            int iconRes = a.getResourceId(R.styleable.StatCardView_statIcon, 0);

            if (title != null) tvTitle.setText(title);
            if (value != null) tvValue.setText(value);
            if (iconRes != 0) ivIcon.setImageResource(iconRes);

            a.recycle();
        }
    }

    public void setValue(String value) {
        tvValue.setText(value);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }
}
