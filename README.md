# TagClickUtil

高仿小红书话题添加功能

1.对字符中任意标签格式进行匹配     
2.对匹配的格式可进行替换    
3.将处理好的字符串放入textview中进行点击响应


效果图:
<br>
![](result.gif)
<br>

## 数据类说明
^!74&&此余之所得也。!^  
<br>
项目中需求的格式
<br>
^!为开始标志符
<br>
!^为结束标识符
<br>
74为标签ID
<br>
&&为ID和内容的连接符

以上格式若想修改，只要再TagClickUtil中替换匹配的正则表达式即可

### 话题类型（可自行修改）
    public static final int TOPIC = 1;// ##普通标签或者话题
    public static final int LOCATION = 2;// ##地址标签
    public static final int PRICE = 3;// ##价格标签
    public static final int BRAND = 4;// ##品牌标签
    public static final int AT = 5;  //@某人

### 正则
   private static final String TOPIC = "(\\^[#$!@]).+?([#!$@]\\^)";   // ##标签正则匹配
