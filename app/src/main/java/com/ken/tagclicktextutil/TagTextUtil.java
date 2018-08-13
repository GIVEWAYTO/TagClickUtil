package com.ken.tagclicktextutil;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者: by KEN on 2018/7/20 18.
 * 邮箱: gr201655@163.com
 */

public class TagTextUtil {

    public static class Section {
        //类型可自己拓展
        public static final int TOPIC = 1;// ##普通标签或者话题
        public static final int LOCATION = 2;// ##地址标签
        public static final int PRICE = 3;// ##价格标签
        public static final int BRAND = 4;// ##品牌标签
        public static final int AT = 5;  //@某人

        private int start;  //文字起始索引
        private int end;    //文字结尾索引
        private int type;   //话题类型
        private String name;//数据返回中的实际话题string
        private int index;  // 标签索引  用于在标签list中查询对应数据
        private int userId;  // @人 中的 userId   如果不是@人  默认为0（该数据没有用）


        Section(int start, int end, int type, String name, int index, int userId) {
            this.start = start;
            this.end = end;
            this.type = type;
            this.name = name;
            this.index = index;
            this.userId = userId;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }

    private static final String TAG = "zgr";

    private static final String TOPIC = "(\\^[#$!@]).+?([#!$@]\\^)";// ##标签正则匹配

    private static final String ALL = TOPIC ;

    public  SpannableStringBuilder getTagContent(String source, final Context context, final TextView textView) {

        final ArrayList<Section> sections = new ArrayList<>();

        //设置正则
        Pattern pattern = Pattern.compile(ALL);
        Matcher matcher = pattern.matcher(source);

        int replaceCount = 0;
        while (matcher.find()) {

            final String topic = matcher.group();

            //处理##话题

            if (topic != null) {
                try {  //由于数据出错的可能性太大  可能用户恰好写上了标签格式  所以捕获异常
                    int start = matcher.start(1);


                    int i = topic.indexOf("&", 2);
                    String index = topic.substring(2, i);
                    int valueIndex;
                    try {
                        valueIndex = Integer.parseInt(index);

                    } catch (Exception e) {
                        continue;
                    }
                    int last;
                    String typeString;
                    int type;
                    //对多种不同的标签进行 前置符处理，根据项目需求更改
                    if (topic.endsWith("#^")) {
                        typeString = "#";
                        type = Section.TOPIC;
                        last = topic.lastIndexOf("#");
                    } else if (topic.endsWith("$^")) {
                        typeString = "$";
                        type = Section.PRICE;
                        last = topic.lastIndexOf("$");
                    } else if (topic.endsWith("!^")) {
                        type = Section.BRAND;
                        last = topic.lastIndexOf("!");
                        typeString = "!";
                    } else {
                        type = Section.AT;
                        last = topic.lastIndexOf("@");
                        typeString = "@";
                    }
                    String content = topic.substring(i + 2, last);
                    int realStart;

                    realStart = start - replaceCount;

                    source = source.replace(matcher.group(), typeString + content);

                    Section section = new Section(realStart, realStart + content.length() + 1, type, topic, valueIndex, type == Section.AT ? valueIndex : 0);
                    sections.add(section);
                    replaceCount += topic.length() - content.length() - 1;
                } catch (Exception e) {
                    Log.e(TAG, "匹配异常 == " + e.getMessage());
                }
            }

        }
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(source);
        for (int i = 0; i < sections.size(); i++) {
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLUE);
            spannableStringBuilder.setSpan(foregroundColorSpan, sections.get(i).start, sections.get(i).end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }

        final BackgroundColorSpan span = new BackgroundColorSpan(Color.RED);
        final int slop = ViewConfiguration.get(context).getScaledTouchSlop();
        View.OnTouchListener touchListener = new View.OnTouchListener() {

            int downX, downY;
            Section downSection = null;
            int id;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                Layout layout = textView.getLayout();
                if (layout == null) {
                    Log.d(TAG, "layout is null");
                    return false;
                }
                int line = 0;
                int index = 0;

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        int actionIndex = event.getActionIndex();
                        id = event.getPointerId(actionIndex);
                        downX = (int) event.getX(actionIndex);
                        downY = (int) event.getY(actionIndex);
                        Log.d(TAG, "ACTION_down,x:" + event.getX() + ",y:" + event.getY());
                        line = layout.getLineForVertical(textView.getScrollY() + (int) event.getY());
                        index = layout.getOffsetForHorizontal(line, (int) event.getX());
                        int lastRight = (int) layout.getLineRight(line);
                        Log.d(TAG, "lastRight:" + lastRight);
                        if (lastRight < event.getX()) {  //文字最后为话题时，如果点击在最后一行话题之后，也会造成话题被选中效果
                            return false;
                        }
                        Log.d(TAG, " index:" + index + ",sections:" + sections.size());
                        for (Section section : sections) {
                            if (index >= section.start && index <= section.end) {
                                spannableStringBuilder.setSpan(span, section.start, section.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                downSection = section;
                                textView.setText(spannableStringBuilder);
                                textView.getParent().requestDisallowInterceptTouchEvent(true);//不允许父view拦截
                                Log.d(TAG, "downSection" + downSection.toString());
                                return true;
                            }
                        }

                        return false;
                    case MotionEvent.ACTION_MOVE:
                        int indexMove = event.findPointerIndex(id);
                        int currentX = (int) event.getX(indexMove);
                        int currentY = (int) event.getY(indexMove);
                        Log.d(TAG, "ACTION_MOVE,x:" + currentX + ",y:" + currentY);
                        if (Math.abs(currentX - downX) < slop && Math.abs(currentY - downY) < slop) {
                            if (downSection == null) {
                                Log.d(TAG, "downSection is null");
                                textView.getParent().requestDisallowInterceptTouchEvent(false);//允许父view拦截
                                return false;
                            }

                            break;
                        }
                        downSection = null;
                        textView.getParent().requestDisallowInterceptTouchEvent(false);//允许父view拦截

                    case MotionEvent.ACTION_CANCEL:
                        Log.d(TAG, "ACTION_CANCEL");
                    case MotionEvent.ACTION_UP:
                        int indexUp = event.findPointerIndex(id);
                        spannableStringBuilder.removeSpan(span);
                        textView.setText(spannableStringBuilder);
                        int upX = (int) event.getX(indexUp);
                        int upY = (int) event.getY(indexUp);
                        Log.d(TAG, "ACTION_UP,x:" + upX + ",y:" + upY);
                        if (Math.abs(upX - downX) < slop && Math.abs(upY - downY) < slop) {
                            //TODO 此处回调
                            if (downSection != null) {
                                /**
                                 * 此处回调
                                 */
                                textView.setTag(downSection);
                                textView.performClick();
                                downSection = null;
                            } else {
                                return false;
                            }
                        } else {
                            Log.d(TAG, "false");
                            downSection = null;
                            return false;
                        }
                        break;
                }
                Log.d(TAG, "true");
                return true;
            }
        };
        textView.setOnTouchListener(touchListener);
        return spannableStringBuilder;
    }
}
