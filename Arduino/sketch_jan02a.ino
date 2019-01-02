#include "SIM900.h"
#include <SoftwareSerial.h>
#include <Scheduler.h>
#include "sms.h"
 
SMSGSM sms;
 
int numdata;
boolean started = false; //trạng thái modul sim
char smstext[160]; // nội dung tin nhắn
char number[20]; // số điện thoại format theo định dạng quốc tế
int flashStatus = 0;
int unsigned flashDelay = 0;
int percent = 0;
 
void setup(){
  Serial.begin(9600);
  Serial.println("Gui va nhan tin nhan");
  if (gsm.begin(9600)){
    Serial.println("\nstatus=READY");
    started=true;  
  }
  else Serial.println("\nstatus=IDLE");
  
  if(started){
//    sms.SendSMS("+84336897187", "Online");
  }
  pinMode(13, OUTPUT);
  digitalWrite(13, LOW);
}

void loop() {
  if(flashStatus){
    blink(flashDelay);
  }
  analogWrite(11, percent);
  if(started){
    char pos; //địa chỉ bộ nhớ sim (sim luu tối đa 40 sms nên max pos = 40)
    pos = sms.IsSMSPresent(SMS_UNREAD); // kiểm tra tin nhắn chưa đọc trong bộ nhớ
    //hàm này sẽ trả về giá trị trong khoảng từ 0-40
    if((int)pos){//nêu có tin nhắn chưa đọc
      if(sms.GetSMS(pos, number, smstext, 160)){
        Serial.print("So dien thoại: ");
        Serial.println(number);
        Serial.print("Noi dung tin nhan: ");
        Serial.println(smstext);
        if(strstr(smstext,"LED ON")){//so sánh 2 chuỗi
          digitalWrite(13, HIGH);
          sms.SendSMS("+84336897187", "LED ON");
          Serial.println("Bat led");
        }if(strstr(smstext,"LED OFF")){
          digitalWrite(13, LOW);
          sms.SendSMS("+84336897187", "LED OFF");
          Serial.println("Bat led");
        }if(strstr(smstext,"LED FON")){
          flashStatus = 1;
          sms.SendSMS("+84336897187", "LED FON");
          flashDelay = (smstext[11] & 0xf) + (smstext[10] & 0xf)*10 + (smstext[9] & 0xf)*100 + (smstext[8] & 0xf)*1000;
          Serial.println("Bat nhay led");
        }if(strstr(smstext,"LED FOFF")){
          flashStatus = 0;
          sms.SendSMS("+84336897187", "LED FOFF");
          Serial.println("Tat nhay led");
        }if(strstr(smstext,"LED PUP")){
          if(percent < 255){
            percent += 25;
          }
          
          if(percent == 0){
            sms.SendSMS("+84336897187","LED FP 000");
          }else if(percent == 25){
            sms.SendSMS("+84336897187","LED FP 010");
          }else if(percent == 50){
            sms.SendSMS("+84336897187","LED FP 020");
          }else if(percent == 75){
            sms.SendSMS("+84336897187","LED FP 030");
          }else if(percent == 100){
            sms.SendSMS("+84336897187","LED FP 040");
          }else if(percent == 125){
            sms.SendSMS("+84336897187","LED FP 050");
          }else if(percent == 150){
            sms.SendSMS("+84336897187","LED FP 060");
          }else if(percent == 175){
            sms.SendSMS("+84336897187","LED FP 070");
          }else if(percent == 200){
            sms.SendSMS("+84336897187","LED FP 080");
          }else if(percent == 225){
            sms.SendSMS("+84336897187","LED FP 090");
          }else if(percent == 250){
            sms.SendSMS("+84336897187","LED FP 100");
          }
          Serial.println("Tang percent");
        }if(strstr(smstext,"LED PDOWN")){
          if(percent > 0){
            percent -= 25;
          }
          
          if(percent == 0){
            sms.SendSMS("+84336897187","LED FP 000");
          }else if(percent == 25){
            sms.SendSMS("+84336897187","LED FP 010");
          }else if(percent == 50){
            sms.SendSMS("+84336897187","LED FP 020");
          }else if(percent == 75){
            sms.SendSMS("+84336897187","LED FP 030");
          }else if(percent == 100){
            sms.SendSMS("+84336897187","LED FP 040");
          }else if(percent == 125){
            sms.SendSMS("+84336897187","LED FP 050");
          }else if(percent == 150){
            sms.SendSMS("+84336897187","LED FP 060");
          }else if(percent == 175){
            sms.SendSMS("+84336897187","LED FP 070");
          }else if(percent == 200){
            sms.SendSMS("+84336897187","LED FP 080");
          }else if(percent == 225){
            sms.SendSMS("+84336897187","LED FP 090");
          }else if(percent == 250){
            sms.SendSMS("+84336897187","LED FP 100");
          }
          Serial.println("Giam percent");
        }if(strstr(smstext,"LED PMIN")){
          percent = 0;
          sms.SendSMS("+84336897187","LED FP 000");
          Serial.println("percent max");
        }if(strstr(smstext,"LED PMAX")){
          percent = 255;
          sms.SendSMS("+84336897187","LED FP 100");
          Serial.println("=percent min");
        }if(strstr(smstext,"LED PSET")){
          percent = (smstext[11] & 0xf) + (smstext[10] & 0xf)*10 + (smstext[9] & 0xf)*100;
          sms.SendSMS("+84336897187","LED FP OK");
          Serial.println("Set percent min");
        }else{
          Serial.println("Khong xac dinh");
        }
      }
      sms.DeleteSMS(byte(pos));//xóa sms vừa nhận
    }
  }
}
void blink(int dl){
          delay(dl);
          digitalWrite(13,HIGH);
          delay(dl);
          digitalWrite(13,LOW);
}
