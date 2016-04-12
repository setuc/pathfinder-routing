require 'httparty'
require 'json'
require 'sinatra/base'
require 'sinatra/config_file'
require 'thread'
require 'timeout'

class MasterRouter < Sinatra::Base
  register Sinatra::ConfigFile

  config_file 'config.yml'

  def compute_distance(routes, distances)
    total = 0
    routes.each do |route|
      route.each_cons(2) do |a, b|
        total = total + distances[a-1][b-1]
      end
    end
    total
  end

  post '/' do
    puts 'Route request received'
    request.body.rewind
    request_payload = JSON.parse request.body.read
    distances = request_payload['distances']

    threads = []
    settings.workers.each do |u|
      threads << Thread.new {
        begin
          Thread.current[:output] = HTTParty.post(u, {
            :body => request_payload.to_json,
            :headers => { 'Content-Type' => 'application/json' },
            :timeout => settings.timeout
          }).parsed_response
        rescue TimeoutError
          Thread.current[:output] = nil
        end
      }
    end

    best_route = nil
    best_distance = nil
    threads.each do |t|
      t.join
      unless t[:output].nil?
        route = t[:output]
        distance = compute_distance(route["routes"], distances)
        if best_distance.nil? or distance < best_distance
          best_distance = distance
          best_route = route
        end
      end
    end
    best_route.to_json
  end
end
