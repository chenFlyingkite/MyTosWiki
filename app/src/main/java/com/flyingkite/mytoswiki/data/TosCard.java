package com.flyingkite.mytoswiki.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TosCard {
    //------------
    //---- Basic info, 基礎卡片內容
    //------------

    /** Normalized ID, in form of %04d */
    @SerializedName("idNorm")
    public String idNorm = "";

    /** Http link to wiki page */
    @SerializedName("wikiLink")
    public String wikiLink = "";

    /** Name, like 鳴動威嚴 ‧ 摩迪與曼尼 */
    @SerializedName("name")
    public String name = "";

    /** Attributes, one of 水 火 木 光 暗, like 光 */
    @SerializedName("attribute")
    public String attribute = "";

    /** Icon, image link */
    @SerializedName("icon")
    public String icon = "";

    /** Image of card, image link */
    @SerializedName("bigImage")
    public String bigImage = "";

    /** ID, like 1063  */
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

    /** Max Heath point, like 129 */
    @SerializedName("maxHP")
    public long maxHP;

    /** Max Attack, like 71 */
    @SerializedName("maxAttack")
    public long maxAttack;

    /** Max Recovery, like 24 */
    @SerializedName("maxRecovery")
    public long maxRecovery;

    /** Min Heath point, like 86 */
    @SerializedName("minHP")
    public long minHP;

    /** Min Attack, like 47 */
    @SerializedName("minAttack")
    public long minAttack;

    /** Min Recovery, like 15 */
    @SerializedName("minRecovery")
    public long minRecovery;

    /** Experience curve, like 50 * k 萬, k = 1, 2, ..., 20 */
    @SerializedName("expCurve")
    public int expCurve;

    /** Min exp when eaten by others, like 700 */
    @SerializedName("minExpSacrifice")
    public int minExpSacrifice;

    /** Sacrifice exp raised per Lv, like 1000 */
    @SerializedName("perLvExpSacrifice")
    public int perLvExpSacrifice;

    //------------
    // Economics of Sacrifice of card, index of Happiness
    // Marginal Utility, 邊際效用
    // Total Utility, 總效用
    //------------

    /** Max marginal utility of card level,
     * After level N, TU(N+1) - TU(N) < dExp = perLvExpSacrifice
     * So gained Exp per level < need exp per level*/
    @SerializedName("maxMUPerLevel")
    public int maxMUPerLevel;

    /** Max total utility of card level,
     * After level N, ExpSacrifice(N) < ExpNeed(N),
     * So gained Exp of level < need exp of level */
    @SerializedName("maxTUAllLevel")
    public int maxTUAllLevel;

    //------------
    //---- Card Details, 卡片資訊
    //------------

    /** Evolution to card idNorm */
    @SerializedName("cardDetails")
    public String cardDetails = "";

    //------------
    //---- Active Skill 1, 主動技能1
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
    //---- Active Skill 2, 主動技能2
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
    //---- Leader Skill, 隊長技能
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

    //------------
    //---- Evolution, 卡片進化
    //------------

    /** Evolution from card idNorm */
    @SerializedName("evolveFrom")
    public String evolveFrom = "";

    /** Evolution material card idNorm */
    @SerializedName("evolveNeed")
    public List<String> evolveNeed = new ArrayList<>();

    /** Evolution to card idNorm */
    @SerializedName("evolveTo")
    public String evolveTo = "";

    //------------
    //---- Combination, 卡片合體
    //------------

    /** Combination material cards idNorm */
    @SerializedName("combineFrom")
    public List<String> combineFrom = new ArrayList<>();

    /** Combination material cards idNorm */
    @SerializedName("combineTo")
    public List<String> combineTo = new ArrayList<>();
}

