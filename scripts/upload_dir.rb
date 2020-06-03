#!/bin/ruby
require 'net/sftp'

def delete_me(sftp, file_name)
    puts "Deleting #{file_name}"
    stat = sftp.stat!(file_name)
    if stat.directory?
        sftp.dir.foreach(file_name) do |child|
            if (child.name != '.') && (child.name != '..')
                delete_me(sftp, "#{file_name}/#{child.name}")
            end
        end
        if file_name != "/"
            sftp.rmdir!(file_name)
        end
    else
        sftp.remove!(file_name)
    end
end

puts "Connecting..."
Net::SFTP.start(ENV['SITE_HOST'], ENV['SITE_USER'], :password => ENV['SITE_PASSWORD']) do |sftp|
    puts "Removing old files..."
    delete_me(sftp, "/");
    puts "Uploading new files from #{ARGV[0]}"
    sftp.upload!(ARGV[0], "/")
end
puts "Done"
