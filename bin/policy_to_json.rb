# encoding: utf-8
require('json')

# ****************************************************
# zmdpで出力したハッシュ形式のファイルを読み込み，
# それをjsonとして吐き出す
# 
# 第1引数: 入力ファイル（***.policy）
# ****************************************************


# == ファイルの読み込み
puts "file read: " + ARGV[0]
str = File.read(ARGV[0])

# == ハッシュのキーをシンボルに置換
str.gsub!(/policyType/, ":policyType")
str.gsub!(/numPlanes/, ":numPlanes")
str.gsub!(/planes/, ":planes")
str.gsub!(/action/, ":action")
str.gsub!(/numEntries/, ":numEntries")
str.gsub!(/entries/, ":entries")

# == evalでハッシュとして読み込み，jsonにする
hs = eval str

# == 状態価値ベクトルのサイズを導出
size = 0
hs[:planes].each do |plane|
  if plane[:numEntries] > size then
    size = plane[:numEntries]
  end
end

puts ("max size: " + size.to_s)
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