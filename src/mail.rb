# encoding: utf-8
require 'mail'

# ********************************************************
# 自分宛てにgメールを送信する
# 
# 引数（オプション．なくてもよい）
# ARGV[0]: メールタイトル
# ARGV[1]: メールアドレス
# ARGV[2]: パスワード
#
# [使い方]
# ruby mail.rb "subject" "hoge.gmail" "passwd"
# ********************************************************

mail = Mail.new
address = ARGV[1]
passwd = ARGV[2]
body = Time.now.to_s + "\n"

options = { :address              => "smtp.gmail.com",
            :port                 => 587,
            :domain               => "smtp.gmail.com",
            :user_name            => address,
            :password             => passwd,
            :authentication       => :plain,
            :enable_starttls_auto => true  }
mail.charset = 'utf-8'
mail.from address
mail.to address
mail.subject ARGV[0]
mail.body body
mail.delivery_method(:smtp, options)
mail.deliver