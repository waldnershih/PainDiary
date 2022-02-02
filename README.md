# PainDiary
Personalised pain diary application enables individuals to record their pain experience and related factors such as their pain intensity level, pain location, physical activities (or number of steps taken daily) and mood. It allows users to check the daily record and have a weekly, monthly health report based on those factors

## Features
- Records will be uploaded to **Firebase** 10pm every day
- Records will be stored in the local database(memory of the phone)
- Able to see the current temperature, humidity and pressure 
- Allow users to record daily
  - pain intensity level
  - pain location
  - mood
  - goal of physical activities (number of steps taken)
  - number of steps taken 
- Notify(broadcast) users to keep their daily record
- Analysis report
  - Pain location pie chart
  - Steps taken pie chart
    - How closely they finished(%)
  - Pain and Weather Line chart
    - (Extra)Perform the correlation test

## Development
- Using MVVM architecture
- Written in Java
- Data stored in Room and will upload to Firebase
- Retrieve weather data by using Retrofit from Open Weather API
- Using Mapbox API to develop the map function
