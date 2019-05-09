package com.flyingkite.mytoswiki.data.pack;

import com.flyingkite.mytoswiki.util.TosCardUtil;

public class PackCard {
    public int index;

    public String idNorm;

    public int exp;

    public int lv;

    public int skillLv;

    public long createAt;

    public int soulIfSell;

    public int soulOwned;

    public int refineLv;

    public int skinId;

    public int skillExp;

    public int normalSkillCd;

    public String source;

    public PackCard copy() {
        return TosCardUtil.parseCard(source);
    }

}
