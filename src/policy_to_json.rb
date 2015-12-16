# encoding: utf-8
require('json')

# ********************************************************
# zmdpで出力したハッシュ形式のファイルを読み込み，
# それをjsonとして吐き出す
# 
# 第1引数(ARGV[0]): policyファイル（xxx.policy）
# 第2引数(ARGV[1]): pomdpファイル(xxx.pomdp)
#     * pomdpファイルは状態数（= ベクトル長）のために必要
# ********************************************************

# == pomdpファイルの読み込み
pomdp_path = ARGV[1]
size = 0

# 状態数の取得
File.open(pomdp_path, "r") do |f|
  f.each_line do |line|

    # 行をコロンと空白文字（タブを含む）でsplitし，空要素""を削除
    arr = line.split(/[:\s]/).compact.reject(&:empty?)

    # 先頭文字がstatesの行を取得し，配列長を見ることで状態数を取得
    if arr[0] == "states" then
      size = arr.length - 1
      break;
    end
  end
end

puts ("max sizedayo: " + size.to_s)



# == policyファイルの読み込み
puts "policy file reading... : " + ARGV[0]
str_policy = File.read(ARGV[0])

# == ハッシュのキーをシンボルに置換
str_policy.gsub!(/policyType/, ":policyType")
str_policy.gsub!(/numPlanes/, ":numPlanes")
str_policy.gsub!(/planes/, ":planes")
str_policy.gsub!(/action/, ":action")
str_policy.gsub!(/numEntries/, ":numEntries")
str_policy.gsub!(/entries/, ":entries")

# == evalでハッシュとして読み込み，jsonにする
hs = eval str_policy
print "convert..."

hs[:planes].each do |plane|
  arr = Array.new
  j = 0  # 要素番号
  i = 0     # 価値関数ベクトルの要素i

  # 新しい価値関数ベクトルの作成
  while i < size do
    if (j < plane[:entries].length) then
      if i < plane[:entries][j] then
        arr.push(0.0)
      elsif i == plane[:entries][j] then
        arr.push(plane[:entries][j+1])
        j += 2
      end
    else
      arr.push(0.0)
    end
    i += 1
  end

  # entriesを置き換える
  plane[:entries] = arr
end

# == 書き込み
File.write(ARGV[0]+".json", hs.to_json)
puts "finish"