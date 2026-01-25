# פקודות מהירות - GitHub Upload

## 🚀 העלאה ראשונית (עכשיו!)

```powershell
# 1. וודא שאתה בתיקיית הפרויקט
cd c:\Users\user\idesign4u1\ShoppingListApp

# 2. שנה את שם ה-branch ל-main
git branch -M main

# 3. חבר ל-GitHub Repository שלך
# **החלף YOUR-USERNAME בשם המשתמש שלך!**
git remote add origin https://github.com/YOUR-USERNAME/ShoppingListApp.git

# 4. העלה!
git push -u origin main
```

**אם תקבל שגיאה "remote origin already exists":**
```powershell
git remote remove origin
git remote add origin https://github.com/YOUR-USERNAME/ShoppingListApp.git
git push -u origin main
```

---

## 🔄 עדכונים עתידיים (כל פעם שתשנה משהו)

```powershell
cd c:\Users\user\idesign4u1\ShoppingListApp
git add .
git commit -m "תיאור השינוי"
git push
```

**דוגמאות לתיאורים:**
- `"תיקנתי באג בצ'אט"`
- `"הוספתי כפתור חדש"`
- `"שיפרתי עיצוב"`

---

## ✅ מה כבר עשיתי בשבילך

- ✅ `git init` - אתחלתי Git
- ✅ `git add .` - הוספתי את כל הקבצים
- ✅ `git commit` - יצרתי commit ראשון

**מה נשאר לך:**
1. צור Repository ב-GitHub
2. הרץ את הפקודות למעלה (החלף את שם המשתמש!)
3. הגדר Firebase App Distribution
4. הוסף GitHub Secrets

**הכל מוסבר במדריך המלא ב-`walkthrough.md`!**
