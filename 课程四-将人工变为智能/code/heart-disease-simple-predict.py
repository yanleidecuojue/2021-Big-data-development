import pandas as pd
from sklearn.ensemble import RandomForestClassifier #for the model
from sklearn.model_selection import train_test_split #for data splitting

pd.options.mode.chained_assignment = None  #hide any pandas warnings

dt = pd.read_csv("~/s3data/practice/big-task/dataset/heart.csv")

dt.head(10)

dt = pd.get_dummies(dt, drop_first=True)

dt.head()

X_train, X_test, y_train, y_test = train_test_split(dt.drop('target', 1), dt['target'], test_size = .2, random_state=10) #split the data

model = RandomForestClassifier(max_depth=5)
model.fit(X_train, y_train)

y_predict = model.predict(X_test)
y_pred_quant = model.predict_proba(X_test)[:, 1]
y_pred_bin = model.predict(X_test)
output = X_test
output['predict'] = y_pred_bin
path = "~/s3data/practice/big-task/output/output.csv"
output.to_csv(path, index=False)
