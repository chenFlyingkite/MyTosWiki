package com.flyingkite.mytoswiki.data.tos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TosCard implements Parcelable {
    //------------
    //---- Basic info, 基礎卡片內容
    //------------

    /** Normalized ID, in form of %04d */
    @SerializedName(TC.idNorm)
    public String idNorm = "";

    /** Http link to wiki page */
    @SerializedName(TC.wikiLink)
    public String wikiLink = "";

    /** Name, like 鳴動威嚴 ‧ 摩迪與曼尼 */
    @SerializedName(TC.name)
    public String name = "";

    /** Attributes, one of 水 火 木 光 暗, like 光 */
    @SerializedName(TC.attribute)
    public String attribute = "";

    /** Icon, image link */
    @SerializedName(TC.icon)
    public String icon = "";

    /** Image of card, image link */
    @SerializedName(TC.bigImage)
    public String bigImage = "";

    /** ID, like 1063  */
    @SerializedName(TC.id)
    public String id = "";

    /** Rarity, star 1 ~ 8 */
    @SerializedName(TC.rarity)
    public int rarity;

    /** Cost of space, like 16 */
    @SerializedName(TC.cost)
    public int cost;

    /** Race, like 人 */
    @SerializedName(TC.race)
    public String race = "";

    /** Series, like 北域遺族 */
    @SerializedName(TC.series)
    public String series = "";

    /** Lv max, like 99 */
    @SerializedName(TC.LvMax)
    public int LvMax;

    /** MaxExp, like 500_0000 */
    @SerializedName(TC.ExpMax)
    public long ExpMax;

    /** Max Heath point, like 129 */
    @SerializedName(TC.maxHP)
    public long maxHP;

    /** Max Attack, like 71 */
    @SerializedName(TC.maxAttack)
    public long maxAttack;

    /** Max Recovery, like 24 */
    @SerializedName(TC.maxRecovery)
    public long maxRecovery;

    //------------
    //---- Amelioration benefit, 昇華提升能力
    //---- on HP, Attack, Recovery, Skill CD
    //------------
    /** Ame + Max Heath point, like 129 */
    @SerializedName(TC.maxHPAme)
    public long maxHPAme;

    /** Ame + Max Attack, like 71 */
    @SerializedName(TC.maxAttackAme)
    public long maxAttackAme;

    /** Ame + Max Recovery, like 24 */
    @SerializedName(TC.maxRecoveryAme)
    public long maxRecoveryAme;

    /** Min Heath point, like 86 */
    @SerializedName(TC.minHP)
    public long minHP;

    /** Min Attack, like 47 */
    @SerializedName(TC.minAttack)
    public long minAttack;

    /** Min Recovery, like 15 */
    @SerializedName(TC.minRecovery)
    public long minRecovery;

    /** Experience curve, like 50 * k 萬, k = 1, 2, ..., 20 */
    @SerializedName(TC.expCurve)
    public int expCurve;

    /** Min exp when eaten by others, like 700 */
    @SerializedName(TC.minExpSacrifice)
    public int minExpSacrifice;

    /** Sacrifice exp raised per Lv, like 1000 */
    @SerializedName(TC.perLvExpSacrifice)
    public int perLvExpSacrifice;

    //------------
    // Economics of Sacrifice of card, index of Happiness
    // Marginal Utility, 邊際效用
    // Total Utility, 總效用
    //------------

    /** Max marginal utility of card level,
     * After level N, TU(N+1) - TU(N) < dExp = perLvExpSacrifice
     * So gained Exp per level < need exp per level*/
    @SerializedName(TC.maxMUPerLevel)
    public int maxMUPerLevel;

    /** Max total utility of card level,
     * After level N, ExpSacrifice(N) < ExpNeed(N),
     * So gained Exp of level < need exp of level */
    @SerializedName(TC.maxTUAllLevel)
    public int maxTUAllLevel;

    /**
     * Add on HP if LV = 99, skill = max
     */
    @SerializedName(TC.allMaxAddHp)
    public int allMaxAddHp;

    /**
     * Add on Attack if LV = 99, skill = max
     */
    @SerializedName(TC.allMaxAddAttack)
    public int allMaxAddAttack;

    /**
     * Add on Recovery if LV = 99, skill = max
     */
    @SerializedName(TC.allMaxAddRecovery)
    public int allMaxAddRecovery;

    //------------
    //---- Card Details, 卡片資訊
    //------------

    /** Card info, have 組合技，隊長技 */
    @SerializedName(TC.cardDetails)
    public String cardDetails = "";

    //------------
    //---- Leader Skill, 隊長技能
    //------------

    /** Leader skill name, like "光之狂怒" */
    @SerializedName(TC.skillLeaderName)
    public String skillLeaderName = "";

    /** Leader skill description, like "光屬性攻擊力 2.5 倍" */
    @SerializedName(TC.skillLeaderDesc)
    public String skillLeaderDesc = "";

    /** Active skill CD max = skillCDMax1 + amelioration, see #1261 拉法葉爾 */
    @SerializedName(TC.skillCDMaxAme)
    public int skillCDMaxAme;

    //------------
    //---- Active Skill 1, 主動技能1
    //------------

    /** Active skill name, like "木光移魂" */
    @SerializedName(TC.skillName1)
    public String skillName1 = "";

    /** Active skill description, like "木符石轉化為光符石" */
    @SerializedName(TC.skillDesc1)
    public String skillDesc1 = "";

    /** Active skill CD min Lv, like 16 */
    @SerializedName(TC.skillCDMin1)
    public int skillCDMin1;

    /** Active skill CD max Lv, like 5 */
    @SerializedName(TC.skillCDMax1)
    public int skillCDMax1;

    //------------
    //---- Active Skill 2, 主動技能2
    //------------

    /** Active skill 2 name, like "天雷極地" */
    @SerializedName(TC.skillName2)
    public String skillName2 = "";

    /** Active skill 2 description, like "1 回合內，敵方全體轉為暗屬性，並提升光屬性對暗屬性目標的攻擊力" */
    @SerializedName(TC.skillDesc2)
    public String skillDesc2 = "";

    /** Active skill 2 CD min Lv, like 21 */
    @SerializedName(TC.skillCDMin2)
    public int skillCDMin2;

    /** Active skill 2 CD max Lv, like 10 */
    @SerializedName(TC.skillCDMax2)
    public int skillCDMax2;

    /** Switching cards, 變身, like "明鏡之諜 ‧ 冰花"  #1166 -> #1167 */
    @SerializedName(TC.switchChange)
    public String switchChange = "";

    //------------
    //---- Amelioration Skill, 昇華技能
    //------------

    /** Amelioration battle name, like "星辰所挑選 ‧ 加斯陀與波魯克斯" */
    @SerializedName(TC.skillAmeBattleName)
    public String skillAmeBattleName = "";

    /** Active skill 2 description, like "http://zh.tos.wikia.com/wiki/%E6%98%9F%E8%BE%B0%E6%89%80%E6%8C%91%E9%81%B8_%E2%80%A7_%E5%8A%A0%E6%96%AF%E9%99%80%E8%88%87%E6%B3%A2%E9%AD%AF%E5%85%8B%E6%96%AF" */
    @SerializedName(TC.skillAmeBattleLink)
    public String skillAmeBattleLink = "";

    /** Amelioration skill name I, 昇華1名稱 */
    @SerializedName(TC.skillAmeName1)
    public String skillAmeName1 = "";

    /** Amelioration cost, 昇華1靈魂 */
    @SerializedName(TC.skillAmeCost1)
    public int skillAmeCost1;

    /** Amelioration skill name II, 昇華2名稱 */
    @SerializedName(TC.skillAmeName2)
    public String skillAmeName2 = "";

    /** Amelioration cost, 昇華2靈魂 */
    @SerializedName(TC.skillAmeCost2)
    public int skillAmeCost2;

    /** Amelioration skill name III, 昇華3名稱 */
    @SerializedName(TC.skillAmeName3)
    public String skillAmeName3 = "";

    /** Amelioration cost, 昇華3靈魂 */
    @SerializedName(TC.skillAmeCost3)
    public int skillAmeCost3;

    /** Amelioration skill name IV, 昇華4名稱 */
    @SerializedName(TC.skillAmeName4)
    public String skillAmeName4 = "";

    /** Amelioration cost, 昇華4靈魂 */
    @SerializedName(TC.skillAmeCost4)
    public int skillAmeCost4;

    //------------
    //---- Awakening Recall Skill, 極限突破技能
    //------------

    /** Awakening Recall skill name, like "每次消除 1 組 5 粒或以上的心符石，自身攻擊力提升，最大提升至 6 倍，效果持續至戰鬥結束" */
    @SerializedName(TC.skillAwkName)
    public String skillAwkName = "";

    /** Awakening Recall skill name, like "每次消除 1 組 5 粒或以上的心符石，自身攻擊力提升，最大提升至 6 倍，效果持續至戰鬥結束" */
    @SerializedName(TC.skillAwkBattleName)
    public String skillAwkBattleName = "";

    /** Awakening Recall skill name, like "http://zh.tos.wikia.com/wiki/%E9%97%87%E4%B9%8B%E9%9B%99%E5%AD%90%E5%AE%AE#.E4.B8.8D.E6.96.B7.E7.9A.84.E6.86.B6.E5.BF.B5" */
    @SerializedName(TC.skillAwkBattleLink)
    public String skillAwkBattleLink = "";

    //------------
    //---- Power Release stage, 潛能解放關卡
    //------------

    /** Power Release stages */
    @SerializedName(TC.skillPowBattle)
    public List<NameLink> skillPowBattle = new ArrayList<>();

    //------------
    //---- Virtual Rebirth stage, 異空轉生關卡
    //------------

    /** Virtual Rebirth stage, like "迷茫的岔路 ‧ 暗" */
    @SerializedName(TC.skillVirBattleName)
    public String skillVirBattleName = "";

    /** Virtual Rebirth stage, like "http://zh.tos.wikia.com/wiki/%E8%BF%B7%E8%8C%AB%E7%9A%84%E5%B2%94%E8%B7%AF_%E2%80%A7_%E6%9A%97" */
    @SerializedName(TC.skillVirBattleLink)
    public String skillVirBattleLink = "";

    /** Virtual Rebirth from card, like "傾世媚狐 ‧ 蘇妲己" #595 */
    @SerializedName(TC.rebirthFrom)
    public String rebirthFrom = "";

    /** Virtual Rebirth change, like "善心狐仙 ‧ 蘇妲己" #1090 */
    @SerializedName(TC.rebirthChange)
    public String rebirthChange = "";

    //------------
    //---- Combination, 卡片合體
    //------------

    /** Combination material cards idNorm */
    @SerializedName(TC.combineFrom)
    public List<String> combineFrom = new ArrayList<>();

    /** Combination material cards idNorm */
    @SerializedName(TC.combineTo)
    public List<String> combineTo = new ArrayList<>();

    //------------
    //---- Evolution, 卡片進化
    //------------

    @SerializedName(TC.evolveInfo)
    public List<Evolve> evolveInfo = new ArrayList<>();

    //------------
    //---- Skill change, 技能變更
    //------------

    /** Skill change contents */
    @SerializedName(TC.skillChange)
    public List<SkillLite> skillChange = new ArrayList<>();

    /** Skill eating cards, from cardDetail */
    @SerializedName(TC.sameSkills)
    public List<String> sameSkills = new ArrayList<>();

    /** Arm crafts, like #3101 #3102 #3103 #3104 #3105 */
    @SerializedName(TC.armCrafts)
    public List<String> armCrafts = new ArrayList<>();

    //-- For parcelable
    private static final Gson gson = new Gson();

    public static final Parcelable.Creator<TosCard> CREATOR = new Parcelable.Creator<TosCard>() {
        @Override
        public TosCard createFromParcel(Parcel in) {
            return gson.fromJson(in.readString(), TosCard.class);
        }

        @Override
        public TosCard[] newArray(int size) {
            return new TosCard[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String s = gson.toJson(this);
        dest.writeString(s);
    }
    //-- For parcelable - End

    public boolean ameAddHP() {
        return maxHPAme > maxHP;
    }

    public boolean ameAddAttack() {
        return maxAttackAme > maxAttack;
    }

    public boolean ameAddRecovery() {
        return maxRecoveryAme > maxRecovery;
    }

    public boolean ameAddAll() {
        return ameAddHP() || ameAddAttack() || ameAddRecovery();
    }

    public String skillsDesc() {
        return skillDesc1 + " & " + skillDesc2;
    }

    public boolean ameMinusCD() {
        return skillCDMaxAme < skillCDMax1;
    }

    public boolean maxAddHp() {
        return allMaxAddHp > 0;
    }

    public boolean maxAddAttack() {
        return allMaxAddAttack > 0;
    }

    public boolean maxAddRecovery() {
        return allMaxAddRecovery > 0;
    }

    public boolean maxAddAll() {
        return maxAddHp() || maxAddAttack() || maxAddRecovery();
    }

    public long maxHp() {
        return maxHPAme + allMaxAddHp;
    }

    public long maxAttack() {
        return maxAttackAme + allMaxAddAttack;
    }

    public long maxRecovery() {
        return maxRecoveryAme + allMaxAddRecovery;
    }

    public long maxHAR() {
        return maxHp() + maxAttack() + maxRecovery();
    }

    @Override
    public String toString() {
        return String.format("#%s %s (%s %s★ %s)", idNorm, name, attribute, rarity, race);
    }
}
