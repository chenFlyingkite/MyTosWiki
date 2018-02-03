package com.flyingkite;

import com.google.gson.annotations.SerializedName;

public class TosCardN {
    @SerializedName("wikiLink")
    public String wikiLink = "";

    /** Name, like 鳴動威嚴 ‧ 摩迪與曼尼 */
    @SerializedName("name")
    public String name = "";

    /** Attributes, one of 水 火 木 光 暗, like 光 */
    @SerializedName("attribute")
    public String attribute = "";

    /** Icon, like 1063  */
    @SerializedName("icon")
    public String icon = "";

    /** Rarity, star 1 ~ 8 */
    @SerializedName("bigImage")
    public String bigImage = "";

    /** Name, like 1063  */
    @SerializedName("id")
    public String id = "";

    /** Rarity, star 1 ~ 8 */
    @SerializedName("rarity")
    public int rarity;

    /** Cost of space, like 16 */
    @SerializedName("cost")
    public int cost;

    /** Race, like 人 */
    @SerializedName("race")
    public String race = "";

    /** Series, like 北域遺族 */
    @SerializedName("series")
    public String series = "";

    /** Lv max, like 99 */
    @SerializedName("LvMax")
    public int LvMax;

    /** MaxExp, like 500_0000 */
    @SerializedName("ExpMax")
    public long ExpMax;

    //------------
    //---- Active Skill 1
    //------------

    /** Active skill name, like "木光移魂" */
    @SerializedName("skillName")
    public String skillName = "";

    /** Active skill description, like "木符石轉化為光符石" */
    @SerializedName("skillDesc")
    public String skillDesc = "";

    /** Active skill CD min Lv, like 16 */
    @SerializedName("skillCDMin")
    public int skillCDMin;

    /** Active skill CD max Lv, like 5 */
    @SerializedName("skillCDMax")
    public int skillCDMax;

    //------------
    //---- Active Skill 2
    //------------

    /** Active skill 2 name, like "天雷極地" */
    @SerializedName("skillName2")
    public String skillName2 = "";

    /** Active skill 2 description, like "1 回合內，敵方全體轉為暗屬性，並提升光屬性對暗屬性目標的攻擊力" */
    @SerializedName("skillDesc2")
    public String skillDesc2 = "";

    /** Active skill 2 CD min Lv, like 21 */
    @SerializedName("skillCDMin2")
    public int skillCDMin2;

    /** Active skill 2 CD max Lv, like 10 */
    @SerializedName("skillCDMax2")
    public int skillCDMax2;

    //------------
    //---- Leader Skill
    //------------

    /** Leader skill name, like "光之狂怒" */
    @SerializedName("skillLeaderName")
    public String skillLeaderName = "";

    /** Leader skill description, like "光屬性攻擊力 2.5 倍" */
    @SerializedName("skillLeaderDesc")
    public String skillLeaderDesc = "";

    /** Amelioration skill name I, 昇華1名稱 */
    @SerializedName("skillAmeName1")
    public String skillAmeliorationName1 = "";

    /** Amelioration cost, 昇華1靈魂 */
    @SerializedName("skillAmeCost1")
    public int skillAmeliorationCost1;

    /** Amelioration skill name II, 昇華2名稱 */
    @SerializedName("skillAmeName2")
    public String skillAmeliorationName2 = "";

    /** Amelioration cost, 昇華2靈魂 */
    @SerializedName("skillAmeCost2")
    public int skillAmeliorationCost2;

    /** Amelioration skill name III, 昇華3名稱 */
    @SerializedName("skillAmeName3")
    public String skillAmeliorationName3 = "";

    /** Amelioration cost, 昇華3靈魂 */
    @SerializedName("skillAmeCost3")
    public int skillAmeliorationCost3;

    /** Amelioration skill name IV, 昇華4名稱 */
    @SerializedName("skillAmeName4")
    public String skillAmeliorationName4 = "";

    /** Amelioration cost, 昇華4靈魂 */
    @SerializedName("skillAmeCost4")
    public int skillAmeliorationCost4;
}
