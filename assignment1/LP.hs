module LP where

import Data.List
import Data.Maybe

vs = ["s", "a","x","y","z","w","t"]
es = [("s", "w"), ("w","z"),("z","t"),("t","w"),("w","x"),("x","a"),("a","w"),("a","y"),("y","t"),("t","a")]

ys = ["y_{" ++ u ++ v ++ "}" | (u,v) <- es \\ [("s", "w")] ]
zs = concat $ [["z_{" ++ v ++ "}", "z'_{" ++ v ++ "}"] | v <- vs \\ ["s","t"]]

dualVars = ys ++ zs

pythonvars = [[c] | c <- ['a'..]]

n = -1

--    sw wz zt tw wx xa aw ay yt ta
a = [[0, 1, 0, 0, 0, 0, 0, 0, 0, 0]
    ,[0, 0, 1, 0, 0, 0, 0, 0, 0, 0]
    ,[0, 0, 0, 1, 0, 0, 0, 0, 0, 0]
    ,[0, 0, 0, 0, 1, 0, 0, 0, 0, 0]
    ,[0, 0, 0, 0, 0, 1, 0, 0, 0, 0]
    ,[0, 0, 0, 0, 0, 0, 1, 0, 0, 0]
    ,[0, 0, 0, 0, 0, 0, 0, 1, 0, 0]
    ,[0, 0, 0, 0, 0, 0, 0, 0, 1, 0]
    ,[0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
    ,[0, 0, 0, 0, 0, 1, n, n, 0, 1]
    ,[0, 0, 0, 0, 0, n, 1, 1, 0, n]
    ,[0, 0, 0, 0, 1, n, 0, 0, 0, 0]
    ,[0, 0, 0, 0, n, 1, 0, 0, 0, 0]
    ,[0, 0, 0, 0, 0, 0, 0, 1, n, 0]
    ,[0, 0, 0, 0, 0, 0, 0, n, 1, 0]
    ,[0, 1, n, 0, 0, 0, 0, 0, 0, 0]
    ,[0, n, 1, 0, 0, 0, 0, 0, 0, 0]
    ,[1, n, 0, 1, n, 0, 0, 0, 0, 0]
    ,[n, 1, 0, n, 1, 0, 0, 0, 0, 0]]

a' = transpose a

makeLine l = concat $ intersperse "  " $ catMaybes $ zipWith filterVar dualVars l
    where
      filterVar v 0 = Nothing
      filterVar v 1 = Just v
      filterVar v (-1) = Just $ "-" ++ v

init' v p = p ++ " = LpVariable(\"" ++ v ++ "\", 0, None)"

makeConstraint l = let
      initlists = [Just "nlist = []", Just "plist = []"]
      listadds = zipWith3 addToList l dualVars pythonvars
      addtoprob = [Just "prob += lpSum(plist) - lpSum(nlist) >= 0"]
    in concat $ intersperse "\n" $ catMaybes $ initlists ++ listadds ++ addtoprob
    where addToList 0 _ _ = Nothing
          addToList (-1) v p = Just $ "nlist += " ++ p
          addToList 1 v p = Just $ "plist += " ++ p


main = do
  mapM_ putStrLn $ zipWith init' dualVars pythonvars
  mapM_ putStrLn $ map makeConstraint a'
--  mapM_ putStrLn $ map makeLine a'