package com.charles.sharesdk.plugin.extension;

import org.gradle.api.Action;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;

public class ShareExt {
    public ShareConfig debug;
    public ShareConfig release;

    public void debug(Closure closure) {
        debug(ConfigureUtil.configureUsing(closure));
    }

    public void debug(Action<? super ShareConfig> action) {
        if (debug == null) {
            debug = new ShareConfig();
        }
        action.execute(debug);
    }

    public void release(Closure closure) {
        release(ConfigureUtil.configureUsing(closure));
    }

    public void release(Action<? super ShareConfig> action) {
        if (release == null) {
            release = new ShareConfig();
        }
        action.execute(release);
    }

    @Override
    public String toString() {
        return "ShareExt{" +
                "debug=" + debug +
                ", release=" + release +
                '}';
    }

    public static class ShareConfig {
        public ConfigExt qq;
        public ConfigExt wechat;
        public ConfigExt weibo;
        public ConfigExt alipay;
        public ConfigExt dingtalk;
        public ConfigExt facebook;
        public ConfigExt twitter;
        public ConfigExt whatsapp;

        public void qq(Closure closure) {
            qq(ConfigureUtil.configureUsing(closure));
        }

        public void qq(Action<? super ConfigExt> action) {
            if (qq == null) {
                qq = new ConfigExt();
            }
            action.execute(qq);
        }

        public ConfigExt qq() {
            if (qq == null) {
                return new ConfigExt(false);
            }
            return qq;
        }

        public void wechat(Closure closure) {
            wechat(ConfigureUtil.configureUsing(closure));
        }

        public void wechat(Action<? super ConfigExt> action) {
            if (wechat == null) {
                wechat = new ConfigExt();
            }
            action.execute(wechat);
        }

        public ConfigExt wechat() {
            if (wechat == null) {
                return new ConfigExt(false);
            }
            return wechat;
        }

        public void weibo(Closure closure) {
            weibo(ConfigureUtil.configureUsing(closure));
        }

        public void weibo(Action<? super ConfigExt> action) {
            if (weibo == null) {
                weibo = new ConfigExt();
            }
            action.execute(weibo);
        }

        public ConfigExt weibo() {
            if (weibo == null) {
                return new ConfigExt(false);
            }
            return weibo;
        }

        public void alipay(Closure closure) {
            alipay(ConfigureUtil.configureUsing(closure));
        }

        public void alipay(Action<? super ConfigExt> action) {
            if (alipay == null) {
                alipay = new ConfigExt();
            }
            action.execute(alipay);
        }

        public ConfigExt alipay() {
            if (alipay == null) {
                return new ConfigExt(false);
            }
            return alipay;
        }

        public void dingtalk(Closure closure) {
            dingtalk(ConfigureUtil.configureUsing(closure));
        }

        public void dingtalk(Action<? super ConfigExt> action) {
            if (dingtalk == null) {
                dingtalk = new ConfigExt();
            }
            action.execute(dingtalk);
        }

        public ConfigExt dingtalk() {
            if (dingtalk == null) {
                return new ConfigExt(false);
            }
            return dingtalk;
        }

        public void facebook(Closure closure) {
            facebook(ConfigureUtil.configureUsing(closure));
        }

        public void facebook(Action<? super ConfigExt> action) {
            if (facebook == null) {
                facebook = new ConfigExt();
            }
            action.execute(facebook);
        }

        public ConfigExt facebook() {
            if (facebook == null) {
                return new ConfigExt(false);
            }
            return facebook;
        }

        public void twitter(Closure closure) {
            twitter(ConfigureUtil.configureUsing(closure));
        }

        public void twitter(Action<? super ConfigExt> action) {
            if (twitter == null) {
                twitter = new ConfigExt();
            }
            action.execute(twitter);
        }

        public ConfigExt twitter() {
            if (twitter == null) {
                return new ConfigExt(false);
            }
            return twitter;
        }


        public void whatsapp(Closure closure) {
            whatsapp(ConfigureUtil.configureUsing(closure));
        }

        public void whatsapp(Action<? super ConfigExt> action) {
            if (whatsapp == null) {
                whatsapp = new ConfigExt();
            }
            action.execute(whatsapp);
        }

        public ConfigExt whatsapp() {
            if (whatsapp == null) {
                return new ConfigExt(false);
            }
            return whatsapp;
        }


        @Override
        public String toString() {
            return "{" +
                    "qq=" + qq +
                    ", wechat=" + wechat +
                    ", weibo=" + weibo +
                    ", alipay=" + alipay +
                    ", dingtalk=" + dingtalk +
                    ", facebook=" + facebook +
                    '}';
        }
    }
}
