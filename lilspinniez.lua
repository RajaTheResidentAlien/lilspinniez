-- lilspinniez (-by raja)
-- utilizes 'shado' 
-- by Nick Rothwell
-- https://cassiel.com
engine.name = 'LilSpinniez'; mu = require("musicutil")
types = require 'lilspinniez/lib/typez'; blocks = require 'lilspinniez/lib/blockz'
frames = require 'lilspinniez/lib/framez'; renderers = require 'lilspinniez/lib/rendererz'
manager = require 'lilspinniez/lib/manger'
xpos=0; ypos=0; sx=3; centerX=4; centerY=4; bdiv=0.0625; st=0; angl=0
lamp_full = types.LampState.ON; lamp_dull = types.LampState:new(0.5, 0); lamp_off = types.LampState.OFF
block = blocks.Block:new(3, 3):fill(lamp_dull); frame = frames.Frame:new():add(block, 1, 1)
-- Attach shado machinery to grid:
local g = grid.connect()
local renderer = renderers.VariableBlockRenderer:new(16, 8, g)

function init()
    renderer:render(frame); clock.run(rotate,bdiv)
    local mgr = manager.PressManager:new(frame)
    g.key = function (x, y, how) mgr:press(x, y, how); renderer:render(frame) end engine.rvset(0)
end

function rotate()
  local x1, y1, anglr,choos,lmp
  while true do
  clock.sync(bdiv); angl = (angl + (11.25*st)) % 360; anglr = util.degs_to_rads(angl)
  x1 = (sx * math.cos(anglr)) + math.sin(anglr); y1 = (sx * math.sin(anglr)) - math.cos(anglr)
  xpos = (x1 + centerX)//1; ypos = (y1 + centerY)//1
  choos=math.random(0,1); if choos>0 then lmp=lamp_dull else lmp=lamp_off end
  block:setLamp(1,1,lmp); block:setLamp(1,3,lmp); block:setLamp(3,1,lmp); block:setLamp(3,3,lmp)
  frame:moveTo(block, xpos, ypos); renderer:render(frame)
    if sx<5 then
    engine.doit((12-ypos*20)+(mu.note_num_to_freq((xpos+12)*4)),math.random(50,80)*0.04,
      math.random(100,2000)*0.01,math.random(100,1000)*0.001,math.random(111,555)*0.001,math.random(10,100)*0.01)
    end --frq,amp,m1,m2,rl,ps
    sx = math.random(1,7); st = 1-st
  end
end

function block:press(x, y, how)
    if how == 1 then self:setLamp(x,y,lamp_full); centerX=math.random(2,12); centerY=math.random(2,6); engine.rv(1)
    else self:setLamp(x, y, lamp_dull); engine.rv(0) end
end
