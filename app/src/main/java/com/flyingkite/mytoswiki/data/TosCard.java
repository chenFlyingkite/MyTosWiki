package com.flyingkite.mytoswiki.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.flyingkite.mytoswiki.room.card.AA;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = AA.CardDB)
public class TosCard implements Parcelable {
    //------------
    //---- Basic info, 基礎卡片內容
    //------------

    /** Normalized ID, in form of %04d */
    @SerializedName(AA.idNorm)
    @PrimaryKey
    @NonNull
    public String idNorm = "";

    /** Http link to wiki page */
    @SerializedName(AA.wikiLink)
    @ColumnInfo
    public String wikiLink = "";

    /** Name, like 鳴動威嚴 ‧ 摩迪與曼尼 */
    @SerializedName(AA.name)
    @ColumnInfo
    public String name = "";

    /** Attributes, one of 水 火 木 光 暗, like 光 */
    @SerializedName(AA.attribute)
    @ColumnInfo
    public String attribute = "";

    /** Icon, image link */
    @SerializedName(AA.icon)
    @ColumnInfo
    public String icon = "";

    /** Image of card, image link */
    @SerializedName(AA.bigImage)
    @ColumnInfo
    public String bigImage = "";

    /** ID, like 1063  */
    @SerializedName(AA.id)
    @ColumnInfo
    public String id = "";

    /** Rarity, star 1 ~ 8 */
    @SerializedName(AA.rarity)
    @ColumnInfo
    public int rarity;

    /** Cost of space, like 16 */
    @SerializedName(AA.cost)
    @ColumnInfo
    public int cost;

    /** Race, like 人 */
    @SerializedName(AA.race)
    @ColumnInfo
    public String race = "";

    /** Series, like 北域遺族 */
    @SerializedName(AA.series)
    @ColumnInfo
    public String series = "";

    /** Lv max, like 99 */
    @SerializedName(AA.LvMax)
    @ColumnInfo
    public int LvMax;

    /** MaxExp, like 500_0000 */
    @SerializedName(AA.ExpMax)
    @ColumnInfo
    public long ExpMax;

    /** Max Heath point, like 129 */
    @SerializedName(AA.maxHP)
    @ColumnInfo
    public long maxHP;

    /** Max Attack, like 71 */
    @SerializedName(AA.maxAttack)
    @ColumnInfo
    public long maxAttack;

    /** Max Recovery, like 24 */
    @SerializedName(AA.maxRecovery)
    @ColumnInfo
    public long maxRecovery;

    /** Min Heath point, like 86 */
    @SerializedName(AA.minHP)
    @ColumnInfo
    public long minHP;

    /** Min Attack, like 47 */
    @SerializedName(AA.minAttack)
    @ColumnInfo
    public long minAttack;

    /** Min Recovery, like 15 */
    @SerializedName(AA.minRecovery)
    @ColumnInfo
    public long minRecovery;

    /** Experience curve, like 50 * k 萬, k = 1, 2, ..., 20 */
    @SerializedName(AA.expCurve)
    @ColumnInfo
    public int expCurve;

    /** Min exp when eaten by others, like 700 */
    @SerializedName(AA.minExpSacrifice)
    @ColumnInfo
    public int minExpSacrifice;

    /** Sacrifice exp raised per Lv, like 1000 */
    @SerializedName(AA.perLvExpSacrifice)
    @ColumnInfo
    public int perLvExpSacrifice;

    //------------
    // Economics of Sacrifice of card, index of Happiness
    // Marginal Utility, 邊際效用
    // Total Utility, 總效用
    //------------

    /** Max marginal utility of card level,
     * After level N, TU(N+1) - TU(N) < dExp = perLvExpSacrifice
     * So gained Exp per level < need exp per level*/
    @SerializedName(AA.maxMUPerLevel)
    @ColumnInfo
    public int maxMUPerLevel;

    /** Max total utility of card level,
     * After level N, ExpSacrifice(N) < ExpNeed(N),
     * So gained Exp of level < need exp of level */
    @SerializedName(AA.maxTUAllLevel)
    @ColumnInfo
    public int maxTUAllLevel;

    //------------
    //---- Card Details, 卡片資訊
    //------------

    /** Evolution to card idNorm */
    @SerializedName(AA.cardDetails)
    @ColumnInfo
    public String cardDetails = "";

    //------------
    //---- Active Skill 1, 主動技能1
    //------------

    /** Active skill name, like "木光移魂" */
    @SerializedName(AA.skillName1)
    @ColumnInfo
    public String skillName1 = "";

    /** Active skill description, like "木符石轉化為光符石" */
    @SerializedName(AA.skillDesc1)
    @ColumnInfo
    public String skillDesc1 = "";

    /** Active skill CD min Lv, like 16 */
    @SerializedName(AA.skillCDMin1)
    @ColumnInfo
    public int skillCDMin1;

    /** Active skill CD max Lv, like 5 */
    @SerializedName(AA.skillCDMax1)
    @ColumnInfo
    public int skillCDMax1;

    //------------
    //---- Active Skill 2, 主動技能2
    //------------

    /** Active skill 2 name, like "天雷極地" */
    @SerializedName(AA.skillName2)
    @ColumnInfo
    public String skillName2 = "";

    /** Active skill 2 description, like "1 回合內，敵方全體轉為暗屬性，並提升光屬性對暗屬性目標的攻擊力" */
    @SerializedName(AA.skillDesc2)
    @ColumnInfo
    public String skillDesc2 = "";

    /** Active skill 2 CD min Lv, like 21 */
    @SerializedName(AA.skillCDMin2)
    @ColumnInfo
    public int skillCDMin2;

    /** Active skill 2 CD max Lv, like 10 */
    @SerializedName(AA.skillCDMax2)
    @ColumnInfo
    public int skillCDMax2;

    //------------
    //---- Leader Skill, 隊長技能
    //------------

    /** Leader skill name, like "光之狂怒" */
    @SerializedName(AA.skillLeaderName)
    @ColumnInfo
    public String skillLeaderName = "";

    /** Leader skill description, like "光屬性攻擊力 2.5 倍" */
    @SerializedName(AA.skillLeaderDesc)
    @ColumnInfo
    public String skillLeaderDesc = "";

    //------------
    //---- Amelioration Skill, 昇華技能
    //------------

    /** Amelioration battle name, like "星辰所挑選 ‧ 加斯陀與波魯克斯" */
    @SerializedName(AA.skillAmeBattleName)
    @ColumnInfo
    public String skillAmeBattleName = "";

    /** Active skill 2 description, like "http://zh.tos.wikia.com/wiki/%E6%98%9F%E8%BE%B0%E6%89%80%E6%8C%91%E9%81%B8_%E2%80%A7_%E5%8A%A0%E6%96%AF%E9%99%80%E8%88%87%E6%B3%A2%E9%AD%AF%E5%85%8B%E6%96%AF" */
    @SerializedName(AA.skillAmeBattleLink)
    @ColumnInfo
    public String skillAmeBattleLink = "";

    /** Amelioration skill name I, 昇華1名稱 */
    @SerializedName(AA.skillAmeName1)
    @ColumnInfo
    public String skillAmeName1 = "";

    /** Amelioration cost, 昇華1靈魂 */
    @SerializedName(AA.skillAmeCost1)
    @ColumnInfo
    public int skillAmeCost1;

    /** Amelioration skill name II, 昇華2名稱 */
    @SerializedName(AA.skillAmeName2)
    @ColumnInfo
    public String skillAmeName2 = "";

    /** Amelioration cost, 昇華2靈魂 */
    @SerializedName(AA.skillAmeCost2)
    @ColumnInfo
    public int skillAmeCost2;

    /** Amelioration skill name III, 昇華3名稱 */
    @SerializedName(AA.skillAmeName3)
    @ColumnInfo
    public String skillAmeName3 = "";

    /** Amelioration cost, 昇華3靈魂 */
    @SerializedName(AA.skillAmeCost3)
    @ColumnInfo
    public int skillAmeCost3;

    /** Amelioration skill name IV, 昇華4名稱 */
    @SerializedName(AA.skillAmeName4)
    @ColumnInfo
    public String skillAmeName4 = "";

    /** Amelioration cost, 昇華4靈魂 */
    @SerializedName(AA.skillAmeCost4)
    @ColumnInfo
    public int skillAmeCost4;

    //------------
    //---- Awakening Recall Skill, 極限突破技能
    //------------

    /** Awakening Recall skill name, like "每次消除 1 組 5 粒或以上的心符石，自身攻擊力提升，最大提升至 6 倍，效果持續至戰鬥結束" */
    @SerializedName(AA.skillAwkName)
    @ColumnInfo
    public String skillAwkName = "";

    /** Awakening Recall skill name, like "每次消除 1 組 5 粒或以上的心符石，自身攻擊力提升，最大提升至 6 倍，效果持續至戰鬥結束" */
    @SerializedName(AA.skillAwkBattleName)
    @ColumnInfo
    public String skillAwkBattleName = "";

    /** Awakening Recall skill name, like "http://zh.tos.wikia.com/wiki/%E9%97%87%E4%B9%8B%E9%9B%99%E5%AD%90%E5%AE%AE#.E4.B8.8D.E6.96.B7.E7.9A.84.E6.86.B6.E5.BF.B5" */
    @SerializedName(AA.skillAwkBattleLink)
    @ColumnInfo
    public String skillAwkBattleLink = "";

    //------------
    //---- Power Release stage, 潛能解放關卡
    //------------

    /** Power Release stage, like "記憶的水鏡" */
    @SerializedName(AA.skillPowBattleName)
    @ColumnInfo
    public String skillPowBattleName = "";

    /** Power Release stage, like "http://zh.tos.wikia.com/wiki/%E8%A8%98%E6%86%B6%E7%9A%84%E6%B0%B4%E9%8F%A1" */
    @SerializedName(AA.skillPowBattleLink)
    @ColumnInfo
    public String skillPowBattleLink = "";

    //------------
    //---- Virtual Rebirth stage, 異空轉生關卡
    //------------

    /** Virtual Rebirth stage, like "迷茫的岔路 ‧ 暗" */
    @SerializedName(AA.skillVirBattleName)
    @ColumnInfo
    public String skillVirBattleName = "";

    /** Virtual Rebirth stage, like "http://zh.tos.wikia.com/wiki/%E8%BF%B7%E8%8C%AB%E7%9A%84%E5%B2%94%E8%B7%AF_%E2%80%A7_%E6%9A%97" */
    @SerializedName(AA.skillVirBattleLink)
    @ColumnInfo
    public String skillVirBattleLink = "";

    //------------
    //---- Evolution, 卡片進化
    //------------

    /** Evolution from card idNorm */
    @SerializedName(AA.evolveFrom)
    @ColumnInfo
    public String evolveFrom = "";

    /** Evolution material card idNorm */
    @SerializedName(AA.evolveNeed)
    @ColumnInfo
    public List<String> evolveNeed = new ArrayList<>();

    /** Evolution to card idNorm */
    @SerializedName(AA.evolveTo)
    @ColumnInfo
    public String evolveTo = "";

    //------------
    //---- Combination, 卡片合體
    //------------

    /** Combination material cards idNorm */
    @SerializedName(AA.combineFrom)
    @ColumnInfo
    public List<String> combineFrom = new ArrayList<>();

    /** Combination material cards idNorm */
    @SerializedName(AA.combineTo)
    @ColumnInfo
    public List<String> combineTo = new ArrayList<>();

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

    // Replace RegEx "public" to "@ColumnInfo\n    public"

    @Override
    public String toString() {
        return String.format("#%s %s (%s %s★ %s)", idNorm, name, attribute, rarity, race);
    }
}
