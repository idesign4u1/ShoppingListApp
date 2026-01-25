# מדריך הפצה אוטומטית - Deployment Guide

מערכת זו מאפשרת עדכונים אוטומטיים של האפליקציה לכל הבודקים.

## 🚀 הגדרה ראשונית (פעם אחת בלבד)

### שלב 1: יצירת חשבון GitHub
1. גש ל-https://github.com
2. לחץ על "Sign up"
3. צור חשבון חינמי

### שלב 2: יצירת Repository
1. לחץ על "New repository"
2. שם: `ShoppingListApp`
3. בחר "Private" (פרטי)
4. לחץ "Create repository"

### שלב 3: העלאת הקוד ל-GitHub
פתח Terminal/PowerShell בתיקיית הפרויקט והרץ:

```bash
git init
git add .
git commit -m "Initial commit - Shopping List App"
git branch -M main
git remote add origin https://github.com/[YOUR-USERNAME]/ShoppingListApp.git
git push -u origin main
```

**החלף `[YOUR-USERNAME]` בשם המשתמש שלך ב-GitHub**

### שלב 4: הגדרת Firebase App Distribution

#### 4.1 הפעלת App Distribution
1. גש ל-https://console.firebase.google.com
2. בחר את הפרויקט שלך
3. בתפריט צד, לחץ על "App Distribution"
4. לחץ "Get Started"

#### 4.2 הוספת בודקים (Testers)
1. לחץ על "Testers & Groups"
2. לחץ "Add Group"
3. שם הקבוצה: `testers`
4. הוסף את האימייל: `hakol4999@gmail.com`
5. שמור

#### 4.3 יצירת Service Account
1. גש ל-https://console.firebase.google.com/project/_/settings/serviceaccounts/adminsdk
2. לחץ "Generate new private key"
3. שמור את הקובץ JSON (אל תשתף אותו!)

#### 4.4 מציאת Firebase App ID
1. בקונסול Firebase, לחץ על גלגל השיניים → Project Settings
2. גלול ל-"Your apps"
3. העתק את ה-"App ID" (מתחיל ב-`1:...`)

### שלב 5: הגדרת GitHub Secrets

1. גש ל-Repository שלך ב-GitHub
2. לחץ "Settings" → "Secrets and variables" → "Actions"
3. לחץ "New repository secret" והוסף 3 secrets:

#### Secret 1: FIREBASE_APP_ID
- Name: `FIREBASE_APP_ID`
- Value: ה-App ID שהעתקת (למשל: `1:123456789:android:abc123`)

#### Secret 2: FIREBASE_SERVICE_ACCOUNT
- Name: `FIREBASE_SERVICE_ACCOUNT`
- Value: תוכן הקובץ JSON שהורדת (פתח בעורך טקסט והעתק הכל)

#### Secret 3: GOOGLE_SERVICES_JSON
- Name: `GOOGLE_SERVICES_JSON`
- Value: הרץ את הפקודה הזו ב-PowerShell:

```powershell
cd c:\Users\user\idesign4u1\ShoppingListApp\app
[Convert]::ToBase64String([IO.File]::ReadAllBytes("google-services.json"))
```

העתק את הפלט והדבק כ-Secret

---

## 📱 שימוש יומיומי

### איך לעדכן את האפליקציה?

1. ערוך קוד בפרויקט
2. העלה ל-GitHub:
```bash
git add .
git commit -m "תיאור השינוי"
git push
```

3. GitHub Actions יבנה אוטומטית את ה-APK
4. ה-APK יועלה ל-Firebase App Distribution
5. הבודקים יקבלו התראה בטלפון!

### איך לראות את הסטטוס?

1. גש ל-Repository ב-GitHub
2. לחץ על "Actions"
3. תראה את כל ה-builds

### איך להוסיף בודקים נוספים?

1. Firebase Console → App Distribution → Testers & Groups
2. הוסף את האימייל לקבוצת `testers`

---

## 🔧 פתרון בעיות

### Build נכשל?
- בדוק ב-GitHub Actions → Logs
- ודא שכל ה-Secrets מוגדרים נכון

### הבודקים לא מקבלים התראה?
- ודא שהאימייל נוסף לקבוצת `testers`
- בדוק ב-Firebase Console → App Distribution → Releases

### שגיאת "google-services.json not found"?
- ודא ש-GOOGLE_SERVICES_JSON מוגדר ב-GitHub Secrets
- בדוק שההצפנה ב-Base64 נכונה

---

## 📞 עזרה נוספת

אם משהו לא עובד, בדוק:
1. GitHub Actions logs
2. Firebase Console → App Distribution
3. ודא שכל ה-Secrets מוגדרים

**זכור:** כל push ל-`main` branch יפעיל build אוטומטי!
