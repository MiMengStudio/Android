# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn com.alipay.sdk.app.H5PayCallback
-dontwarn com.alipay.sdk.app.PayTask
-dontwarn com.chenlb.mmseg4j.ComplexSeg
-dontwarn com.chenlb.mmseg4j.Dictionary
-dontwarn com.chenlb.mmseg4j.MMSeg
-dontwarn com.chenlb.mmseg4j.Seg
-dontwarn com.download.library.DownloadImpl
-dontwarn com.download.library.DownloadListenerAdapter
-dontwarn com.download.library.DownloadTask
-dontwarn com.download.library.ResourceRequest
-dontwarn com.github.houbb.pinyin.constant.enums.PinyinStyleEnum
-dontwarn com.github.promeg.pinyinhelper.Pinyin$Config
-dontwarn com.github.promeg.pinyinhelper.Pinyin
-dontwarn com.github.stuxuhai.jpinyin.PinyinFormat
-dontwarn com.googlecode.aviator.AviatorEvaluator
-dontwarn com.googlecode.aviator.AviatorEvaluatorInstance
-dontwarn com.hankcs.hanlp.HanLP
-dontwarn com.hankcs.hanlp.seg.Segment
-dontwarn com.huaban.analysis.jieba.JiebaSegmenter$SegMode
-dontwarn com.huaban.analysis.jieba.JiebaSegmenter
-dontwarn com.jfirer.jfireel.expression.Expression
-dontwarn com.mayabot.nlp.segment.Lexer
-dontwarn com.mayabot.nlp.segment.Lexers
-dontwarn com.ql.util.express.ExpressRunner
-dontwarn com.rnkrsoft.bopomofo4j.Bopomofo4j
-dontwarn io.github.logtube.Logtube
-dontwarn io.github.logtube.core.IEventLogger
-dontwarn javax.annotation.Nullable
-dontwarn net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
-dontwarn net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
-dontwarn net.sourceforge.pinyin4j.format.HanyuPinyinToneType
-dontwarn net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
-dontwarn org.ansj.splitWord.Analysis
-dontwarn org.ansj.splitWord.analysis.ToAnalysis
-dontwarn org.apache.commons.jexl3.JexlBuilder
-dontwarn org.apache.commons.jexl3.JexlEngine
-dontwarn org.apache.log4j.Logger
-dontwarn org.apache.logging.log4j.LogManager
-dontwarn org.apache.logging.log4j.Logger
-dontwarn org.apache.lucene.analysis.Analyzer
-dontwarn org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer
-dontwarn org.apdplat.word.segmentation.Segmentation
-dontwarn org.apdplat.word.segmentation.SegmentationAlgorithm
-dontwarn org.apdplat.word.segmentation.SegmentationFactory
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.OpenSSLProvider
-dontwarn org.jboss.logging.Logger
-dontwarn org.lionsoul.jcseg.ISegment$Type
-dontwarn org.lionsoul.jcseg.ISegment
-dontwarn org.lionsoul.jcseg.dic.ADictionary
-dontwarn org.lionsoul.jcseg.dic.DictionaryFactory
-dontwarn org.lionsoul.jcseg.fi.SegmenterFunction
-dontwarn org.lionsoul.jcseg.segmenter.SegmenterConfig
-dontwarn org.mozilla.javascript.Context
-dontwarn org.mvel2.MVEL
-dontwarn org.pmw.tinylog.Level
-dontwarn org.pmw.tinylog.Logger
-dontwarn org.slf4j.ILoggerFactory
-dontwarn org.slf4j.Logger
-dontwarn org.slf4j.LoggerFactory
-dontwarn org.slf4j.helpers.NOPLoggerFactory
-dontwarn org.slf4j.spi.LocationAwareLogger
-dontwarn org.springframework.expression.ExpressionParser
-dontwarn org.springframework.expression.spel.standard.SpelExpressionParser
-dontwarn org.tinylog.Level
-dontwarn org.tinylog.Logger
-dontwarn org.tinylog.configuration.Configuration
-dontwarn org.tinylog.format.AdvancedMessageFormatter
-dontwarn org.tinylog.format.MessageFormatter
-dontwarn org.tinylog.provider.LoggingProvider
-dontwarn org.tinylog.provider.ProviderRegistry

-keep public class * extends com.mimeng.base.BaseActivity
-keep public class * extends android.app.Application

-keepclassmembers public class com.mimeng.activity.WebViewActivity$AndroidInterface {
    public <methods>;
}

-keepclassmembers public class com.mimeng.values.* {
    <fields>;
}

-keepclassmembers public class com.mimeng.user.Account {
    <fields>;
}