module LP where

import Data.List
import Data.Maybe

vs = ["s", "a","x","y","z","w","t"]
es = [("s", "w"), ("w","z"),("z","t"),("t","w"),("w","x"),("x","a"),("a","w"),("a","y"),("y","t"),("t","a")]

ys = ["y_{" ++ u ++ v ++ "}" | (u,v) <- es \\ [("s", "w")] ]
zs = concat $ [["z_{" ++ v ++ "}", "z'_{" ++ v ++ "}"] | v <- vs \\ ["s","t"]]

dualVars = ys ++ zs

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
    ,[n, 1, 0, n, 1, 0, 0, 0, 0, 0]
    ,[1, n, 0, 1, n, 0, 0, 0, 0, 0]]

a' = transpose a

makeLine l = concat $ intersperse "  " $ catMaybes $ zipWith filterVar dualVars l
    where
      filterVar v 0 = Nothing
      filterVar v 1 = Just v
      filterVar v (-1) = Just $ "-" ++ v

main = do
  mapM_ (putStrLn . makeLine) a'