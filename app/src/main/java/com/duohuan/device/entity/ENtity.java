package com.duohuan.device.entity;

import java.util.List;

/**
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　             ┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 * 创建人: 杜
 * 日期: 2019/5/18
 * 时间: 19:11
 */
public class ENtity {


    /**
     * measurementEntities : [{"cmUnit":"cm","imageUrl":"https://www.tozmart.com/bndsrv/asset/icon/icon190102/msize01_010.png","inchUnit":"","meaValueCM":110.26729,"meaValueInch":"43(3/8)","sizeIntro":"The maximum horizontal girth with tape-measure passing over the shoulder blades (scapulae) and nipples (the most prominent protrusion of the bra cap for ladies).","sizeName":"Chest girth"},{"cmUnit":"cm","imageUrl":"https://www.tozmart.com/bndsrv/asset/icon/icon190102/msize01_030.png","inchUnit":"","meaValueCM":110.06201,"meaValueInch":"43(3/8)","sizeIntro":"The horizontal girth of natural waistline between top of the hip bones (iliac crests) and the lower ribs.","sizeName":"Waist girth"},{"cmUnit":"cm","imageUrl":"https://www.tozmart.com/bndsrv/asset/icon/icon190102/msize01_040.png","inchUnit":"","meaValueCM":105.69103,"meaValueInch":"41(5/8)","sizeIntro":"The maximum horizontal girth around the torsa taken at the greatest portrusion of the buttocks as viewed from the side.","sizeName":"Hip girth"},{"cmUnit":"cm","imageUrl":"https://www.tozmart.com/bndsrv/asset/icon/icon190102/msize01_060.png","inchUnit":"","meaValueCM":112.10127,"meaValueInch":"44(1/8)","sizeIntro":"The horizontal girth around torso, taken at the midway between the bust level and the crotch level. It is usually the waist level of trousers.","sizeName":"Low waist girth"},{"cmUnit":"cm","imageUrl":"https://www.tozmart.com/bndsrv/asset/icon/icon190102/msize03_010.png","inchUnit":"","meaValueCM":49.614437,"meaValueInch":"19(1/2)","sizeIntro":"Horizontal length with tape-measure between two shoulder points (top of the shoulder joint).","sizeName":"Across shoulder line"},{"cmUnit":"cm","imageUrl":"https://www.tozmart.com/bndsrv/asset/icon/icon190102/msize04_020.png","inchUnit":"","meaValueCM":45.212894,"meaValueInch":"17(3/4)","sizeIntro":"The girth of the neck at 2.5cm (1 inch) above the neck base.","sizeName":"Neck girth"},{"cmUnit":"cm","imageUrl":"https://www.tozmart.com/bndsrv/asset/icon/icon190102/msize09_010.png","inchUnit":"","meaValueCM":64.0409,"meaValueInch":"25(1/4)","sizeIntro":"The girth of the leg below crotch.","sizeName":"Max.thigh girth"},{"cmUnit":"cm","imageUrl":"https://www.tozmart.com/bndsrv/asset/icon/icon190102/msize11_030.png","inchUnit":"","meaValueCM":113.22743,"meaValueInch":"44(5/8)","sizeIntro":"The vertical distance with tape-measure from natural waist level to the floor at side.","sizeName":"Side seam"}]
     * serverStatusCode : 200
     */

    private int serverStatusCode;
    private List<MeasurementEntitiesBean> measurementEntities;

    public int getServerStatusCode() {
        return serverStatusCode;
    }

    public void setServerStatusCode(int serverStatusCode) {
        this.serverStatusCode = serverStatusCode;
    }

    public List<MeasurementEntitiesBean> getMeasurementEntities() {
        return measurementEntities;
    }

    public void setMeasurementEntities(List<MeasurementEntitiesBean> measurementEntities) {
        this.measurementEntities = measurementEntities;
    }

    public static class MeasurementEntitiesBean {
        /**
         * cmUnit : cm
         * imageUrl : https://www.tozmart.com/bndsrv/asset/icon/icon190102/msize01_010.png
         * inchUnit :
         * meaValueCM : 110.26729
         * meaValueInch : 43(3/8)
         * sizeIntro : The maximum horizontal girth with tape-measure passing over the shoulder blades (scapulae) and nipples (the most prominent protrusion of the bra cap for ladies).
         * sizeName : Chest girth
         */

        private String cmUnit;
        private String imageUrl;
        private String inchUnit;
        private double meaValueCM;
        private String meaValueInch;
        private String sizeIntro;
        private String sizeName;

        public String getCmUnit() {
            return cmUnit;
        }

        public void setCmUnit(String cmUnit) {
            this.cmUnit = cmUnit;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getInchUnit() {
            return inchUnit;
        }

        public void setInchUnit(String inchUnit) {
            this.inchUnit = inchUnit;
        }

        public double getMeaValueCM() {
            return meaValueCM;
        }

        public void setMeaValueCM(double meaValueCM) {
            this.meaValueCM = meaValueCM;
        }

        public String getMeaValueInch() {
            return meaValueInch;
        }

        public void setMeaValueInch(String meaValueInch) {
            this.meaValueInch = meaValueInch;
        }

        public String getSizeIntro() {
            return sizeIntro;
        }

        public void setSizeIntro(String sizeIntro) {
            this.sizeIntro = sizeIntro;
        }

        public String getSizeName() {
            return sizeName;
        }

        public void setSizeName(String sizeName) {
            this.sizeName = sizeName;
        }
    }
}
