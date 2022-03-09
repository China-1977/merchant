package work.onss.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PhoneEncryptedData implements Serializable {

    /**
     * phoneNumber : 13580006666
     * purePhoneNumber : 13580006666
     * countryCode : 86
     * watermark : {"appid":"APPID","timestamp":"TIMESTAMP"}
     */
    private String phoneNumber;
    private String purePhoneNumber;
    private String countryCode;
    private WatermarkEntity watermark;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public WatermarkEntity getWatermark() {
        return watermark;
    }

    public void setWatermark(WatermarkEntity watermark) {
        this.watermark = watermark;
    }

    public String getPurePhoneNumber() {
        return purePhoneNumber;
    }

    public void setPurePhoneNumber(String purePhoneNumber) {
        this.purePhoneNumber = purePhoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public static class WatermarkEntity {
        /**
         * appid : APPID
         * timestamp : TIMESTAMP
         */
        private String appid;
        private String timestamp;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
    }
}
