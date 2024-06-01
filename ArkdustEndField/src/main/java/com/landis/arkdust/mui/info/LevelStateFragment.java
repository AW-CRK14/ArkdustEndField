package com.landis.arkdust.mui.info;

import com.landis.arkdust.mui.AbstractArkdustInfoUI;
import com.landis.arkdust.mui.widget.button.EndFieldStyleWidgets;
import com.landis.arkdust.helper.MUIHelper;
import com.landis.arkdust.registry.regtype.ArkdustRegistry;
import com.landis.arkdust.system.world.weather.ClimateParameter;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.fragment.FragmentContainerView;
import icyllis.modernui.fragment.FragmentTransaction;
import icyllis.modernui.text.PrecomputedText;
import icyllis.modernui.text.SpannableString;
import icyllis.modernui.text.Typeface;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.joml.Math;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LevelStateFragment extends AbstractArkdustInfoUI {
    public LevelStateFragment(CompoundTag tag){
        this.info = tag;
    }


    public static final int CHILD_FRAGMENT_INDEX = 1099;
    public FragmentContainerView childFrag;
    public CompoundTag info;
    @Override
    public void addView(LayoutInflater inflater, RelativeLayout layout, DataSet dataSet) {
        childFrag = new FragmentContainerView(getContext());

        //水平选择组件部分
        RadioGroup group = new RadioGroup(getContext());
        group.setOrientation(LinearLayout.HORIZONTAL);
        group.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

        group.addView(new EndFieldStyleWidgets.RadioButtonA(getContext(),group,"Basic",1001));
        group.addView(new EndFieldStyleWidgets.RadioButtonA(getContext(),group,"Weather",1002));
        group.addView(new EndFieldStyleWidgets.RadioButtonA(getContext(),group,"Trend",1003));
        group.addView(new EndFieldStyleWidgets.RadioButtonA(getContext(),group,"Event",1004));

        group.setOnCheckedChangeListener((g,id)->{
            Fragment oldChild = getChildFragmentManager().findFragmentById(CHILD_FRAGMENT_INDEX);
            Fragment child = switch (id){
                case 1001 -> new WeatherFrag(info);
                case 1002,1003,1004 -> new EmptyFrag();
                default -> null;
            };
            if(child != null) {
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                if(oldChild!=null){
                    transaction.remove(oldChild);
                }
                transaction.replace(CHILD_FRAGMENT_INDEX,child)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).setReorderingAllowed(true).commit();
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.setMarginsRelative(0,layout.dp(175),0,0);

        layout.addView(group,params);

        FragmentContainerView child = new FragmentContainerView(getContext());
        child.setId(CHILD_FRAGMENT_INDEX);
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(layout.dp(700),ViewGroup.LayoutParams.MATCH_PARENT);
        p.setMarginsRelative(0,layout.dp(280),0,0);
        p.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(child,p);

    }

    @Override
    public String getTitle() {
        return "gui.arkdust.info.title.level_state";
    }

    public static class EmptyFrag extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
            TextView t = new TextView(getContext());
            LinearLayout l = new LinearLayout(getContext());
            l.addView(t);
            t.setText("empty");
            return l;
        }
    }

    public static class WeatherFrag extends Fragment{
        public final CompoundTag info;
        public WeatherFrag(CompoundTag info){
            this.info = info;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
            LinearLayout layout = new TableLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            //天气信息部分
            {
                LinearLayout left = new LinearLayout(getContext());
                left.setOrientation(LinearLayout.VERTICAL);
                left.setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
                left.setDividerPadding(layout.dp(15));

                //添加天气部分标题
                TextView title = new TextView(getContext());
                title.setText(I18n.get("gui.arkdust.info.title.level_state.climate"));
                title.setTextSize(layout.dp(9));
                title.setTextStyle(Typeface.BOLD);
                left.addView(title);

                //准备数据
                TextView textView = new TextView(getContext());
                List<MUIHelper.TextSpan> spanList = new ArrayList<>();
                String content = "";
                int lastLength = 0;
                ListTag climates = info.getList("climate", 10);
                for (Tag tag : climates) {
                    //获取天气信息
                    ResourceLocation id = new ResourceLocation(((CompoundTag) tag).getString("key"));
                    ClimateParameter parameter = ArkdustRegistry.CLIMATE_PARAMETER.get(id);

                    //构造本条信息的内容
                    float value = ((CompoundTag) tag).getFloat("value");
                    float offset = ((CompoundTag) tag).getFloat("offset");
                    String element = I18n.get("climate." + id.getNamespace() + "." + id.getPath()) + " : " + value;
                    if (offset != 0) {
                        element += (offset > 0 ? "+" : "") + offset;
                    }
                    element += "\n";

                    //如果需要额外染色，则添加进List。
                    float b = Math.clamp(-1, 1, (value + offset) / parameter.range());
                    if (b > 0) {
                        spanList.add(new MUIHelper.TextSpan(lastLength,element.length(),new Color((int) (255 - 3 * b), (int) (255 - 18 * b), (int) (255 - 234 * b))));
                    }

                    //添加内容
                    content += element;
                    lastLength += element.length();
                }

                //添加气候Span信息
                SpannableString spannable = new SpannableString(content);
                spanList.forEach(span -> span.setSpan(spannable));

                textView.setText(PrecomputedText.create(spannable, textView.getTextMetricsParams()), TextView.BufferType.SPANNABLE);

                LinearLayout.LayoutParams p2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                p2.setMarginsRelative(layout.dp(20), 0, 0, 0);
                left.addView(textView, p2);

//                left.setBackground(AbstractArkdustInfoUI.withBorder());


                layout.addView(left, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }


            return layout;
        }
    }
}
