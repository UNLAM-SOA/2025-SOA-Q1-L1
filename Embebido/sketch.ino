#include <Arduino.h>
#include <ESP32Time.h>
#include <time.h>
#include <WiFi.h>

// ---------- BEGIN DEBUG ---------- //
#define SERIAL_DEBUG_ENABLED 1

#if SERIAL_DEBUG_ENABLED
  #define DebugPrint(str)\
    {\
      Serial.println(str);\
    }
#else
  #define DebugPrint(str)
#endif

#define DebugPrintState(st, ev)\
  {\
    String est = st;\
    String evt = ev;\
    String str;\
    str = "-----------------------------------------------------";\
    DebugPrint(str);\
    str = "EST-> [" + est + "]: " + "EVT-> [" + evt + "].";\
    DebugPrint(str);\
    str = "-----------------------------------------------------";\
    DebugPrint(str);\
  }
// ---------- END DEBUG ---------- //


// ---------- BEGIN STATE MACHINE ---------- //
#define MAX_STATES        3
#define MAX_EVENTS        4
#define MAX_TYPE_EVENTS   3

enum states {ST_INIT, ST_IDLE, ST_ROT} current_state;
String s_states [] = {"ST_INIT", "ST_IDLE", "ST_ROTATING"};

enum events {EV_CONT, EV_CFG, EV_IN, EV_TMATCH} new_event;
String s_events [] = {"EV_CONT", "EV_CONFIG", "EV_INPUT", "EV_TIMEMATCH"};

void init_();
void input();
void match();
void none();
void error();

typedef void (*transition)();
transition state_table[MAX_STATES][MAX_EVENTS] = {
  {none,     init_,     none,       none},              // ST_INIT
  {none,     none,      input,      match},             // ST_IDLE
  {none,     none,      none,       none},              // ST_ROTATING
// EV_CONT,  EV_CFG,    EV_INPUT    EV_TMATCH
};

bool wifi_sensor(unsigned long ct);
bool input_listener(unsigned long ct);
bool scheduled(unsigned long ct);

typedef bool (*eventType)(unsigned long ct);
eventType event_type[MAX_TYPE_EVENTS] = {wifi_sensor, input_listener, scheduled};
// ---------- END STATE MACHINE ---------- //

// ---------- BEGIN CONSTANTS ---------- //
#define UMBRAL_DIFF_TIMEOUT   200
#define UMBRAL_DIFF_TIMES     5

#define TS_CHANGE_STATE       50

#define GM_OFFSET_ARG -10800
#define DAY_LIGHT_OFFSET 0 

const char* ssid = "Wokwi-GUEST";
const char* password = "";

const long gmtOffset_sec = GM_OFFSET_ARG;
const int daylightOffset_sec = DAY_LIGHT_OFFSET;
const char* ntpServer = "pool.ntp.org";

#define OFFSET_YEAR 1900
#define OFFSET_MONTH 1
#define CONFIG_TIME -1
// ---------- END CONSTANTS ---------- //

// ---------- BEGIN VARIABLES ---------- //
bool timeout;
long lct;
short last_index_type_sensor  = 0;

struct tm user_time;
ESP32Time rtc;

long ts_wifi                  = 0;
long ts_input                 = -1;
long ts_check_time            = -1;
// ---------- END VARIABLES ---------- //

// ---------- BEGIN ACTIONS ---------- //
void init_(){
  ts_wifi = -1;
  ts_input = 0;

  current_state = ST_IDLE;
}

void input()
{
  ts_input = -1;
  ts_check_time = 0;
}

void match()
{
  ts_check_time = -1;

  current_state = ST_ROT;
}

void none()
{
}

void error(){
  DebugPrint("ERROR DETECTADO!");
}

void do_init()
{
  Serial.begin(115200);
  WiFi.begin(ssid, password);

  DebugPrintState(s_states[current_state], s_events[new_event]);

  current_state = ST_INIT;
  
  timeout = false;
  lct = millis();
}
// ---------- END ACTIONS ---------- //

// ---------- BEGIN EVENTS ---------- //
bool wifi_sensor(unsigned long ct)
{
  if(ts_wifi >= 0)
  {
    int diff = (ct - ts_wifi);
    
    if(diff >= TS_CHANGE_STATE)
    {
      ts_wifi = ct;

      if (WiFi.status() == WL_CONNECTED) 
      {
        //DebugPrint("WIFI conectado");
        struct tm time_system;
        configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
        
        if (getLocalTime(&time_system)) 
        {
          rtc.setTime(mktime(&time_system));
          DebugPrint("Horario del sistema configurado.");

          WiFi.disconnect(true);
          WiFi.mode(WIFI_OFF);
          //DebugPrint("WIFI desconectado");
          

          new_event = EV_CFG;
          return true;
        } 
      }
    }
  }

  return false;
}

bool input_listener(unsigned long ct)
{
  if(ts_input >= 0)
  {
    int diff = (ct - ts_input);
    
    if(diff >= TS_CHANGE_STATE)
    {
      ts_input = ct;

      if (Serial.available() > 0)
      {
        String input_user = Serial.readStringUntil('\n');

        if(is_valid_date(input_user))
        {
            DebugPrint("Fecha y hora almacenadas correctamente.");

            new_event = EV_IN;
            return true;
        } 
        else
        {
          DebugPrint("Fecha inválida");
        }
      }
    }
  }

  return false;
}

bool scheduled(unsigned long ct)
{
  if(ts_check_time >= 0)
  {
    int diff = (ct - ts_check_time);
    
    if(diff >= TS_CHANGE_STATE)
    {
      ts_check_time = ct;

      time_t user_timestamp = mktime(&user_time);
      time_t now = rtc.getEpoch();

      int dt = (now - user_timestamp);
      DebugPrint(dt);
      if(abs(dt) < UMBRAL_DIFF_TIMES)
      {
        new_event = EV_TMATCH;
        return true;
      }
    }
  }

  return false;
}
// ---------- END EVENTS ---------- //

// 
bool is_valid_date(String input) {
  input.trim();

  int user_year, user_month, user_day, user_hour, user_min, user_sec;

  if (sscanf(input.c_str(), "%d-%d-%d %d:%d:%d", 
             &user_year, &user_month, &user_day, &user_hour, &user_min, &user_sec) == 6) 
  {
    user_time.tm_year = user_year - OFFSET_YEAR;
    user_time.tm_mon  = user_month - OFFSET_MONTH;
    user_time.tm_mday = user_day;
    user_time.tm_hour = user_hour;
    user_time.tm_min  = user_min;
    user_time.tm_sec  = user_sec;
    user_time.tm_isdst = CONFIG_TIME;

    /*Serial.printf("%04d-%02d-%02d %02d:%02d:%02d\n",
                  user_year, user_month, user_day, user_hour, user_min, user_sec);*/
    return true;
  } 
  else 
  {
    DebugPrint("Formato inválido. Usa YYYY-MM-DD HH:MM:SS");
    return false;
  }
}

void get_new_event()
{
  short index = 0;
  long ct = millis();
  int diff = (ct - lct);
  timeout = (diff > UMBRAL_DIFF_TIMEOUT)?(true):(false);

  if( timeout )
  {
    timeout = false;
    lct = ct;

    index = (last_index_type_sensor % MAX_TYPE_EVENTS);

    last_index_type_sensor++;

    if(event_type[index](ct))
    {
      return;
    }
  }

  new_event = EV_CONT;
}

void do_event()
{
  get_new_event();

  if((new_event >= 0) && (new_event < MAX_EVENTS) && 
    (current_state >= 0) && (current_state <= MAX_STATES))
  {
    if( new_event != EV_CONT )
    {
      DebugPrintState(s_states[current_state], s_events[new_event]);
    }

    state_table[current_state][new_event]();
  }
  else
  {
    DebugPrint("Error, Estado o Evento fuera de rango.");
  }
}

void setup() 
{
  do_init();
}

void loop() 
{
  do_event();
}