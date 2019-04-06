package com.flyingkite.mytoswiki.library;

import androidx.annotation.IntRange;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntRange(from = Misc.NT_ID_NORM, to = Misc.NT_NAME)
public @interface NameType{}