//Engine_LilSpinniez
//a simple engine made for the app(by raja)
Engine_LilSpinniez : CroneEngine { 
  var pg; var fxg; var fx; var rvrb; var fxroute; var ot=0; var first=1;
                
  *new { arg context, doneCallback;
    ^super.new(context, doneCallback); }
                
  alloc {
    pg = ParGroup.tail(context.xg);
    fxg = ParGroup.after(pg);
    fxroute = Bus.control(context.server, 1).set(0);
    fx = Bus.audio(context.server, 2);
                
    SynthDef(\simpl, { arg out=0, freq=220, amp=0.5, rls=0.4, pos=0, md1=0.5, md2=0.5;
                        var env, osc, sig;
                        env = Env.perc(0.002,rls,amp*0.5).kr(2);
                        osc = MoogFF.ar(LFTri.ar(freq+(md1*FSinOsc.ar(freq*0.6180355))), freq*0.6180355, 2);
                        sig = osc.ring4(FSinOsc.ar(md2*0.6180355)*FSinOsc.ar(md1*0.6180355)) * env;
                        Out.ar(out, Pan2.ar(sig, pos));
                  }).add;
                  
    SynthDef("Reverb", 
       { arg out=context.out_b, noff, fxindx=fx.index;
		      var send; var receive = In.ar(fxindx, 2);
		      var input = Mix.ar(Array.fill(7,{ CombC.ar(receive, 0.1, LFNoise1.kr(0.1.rand, 0.01, 0.05), 4, 0.1) }));
		      input = AllpassN.ar(input, 0.050, [0.050.rand, 0.050.rand], 1)*Lag.kr(noff, 0.01);
		      input = AllpassN.ar(input, 0.050, [0.050.rand, 0.050.rand], 1)*Lag.kr(noff, 0.01);
		      input = AllpassN.ar(input, 0.050, [0.050.rand, 0.050.rand], 1)*Lag.kr(noff, 0.01);
		      input = AllpassN.ar(input, 0.050, [0.050.rand, 0.050.rand], 1)*Lag.kr(noff, 0.01);
		      send = receive + input;
		      Out.ar(out, send);
		}).add;
                
    this.addCommand("doit", "ffffff", { arg msg;
                    var frq = msg[1],am = msg[2],m1 = msg[3],m2 = msg[4],rl = msg[5],ps = msg[6];
                    if(first==0,
                    { ot=context.out_b;
                      Synth(\simpl, [\out,ot,\freq,frq,\amp,am*0.8,\md1,m1,\md2,m2,\rls,rl,\pos,ps], target: pg);
                    },
                    { ot=fx;
                      Synth(\simpl, [\out,ot,\freq,frq,\amp,am*0.4,\md1,m1*2.0,\md2,m2*2.0,\rls,rl*1.5,\pos,ps], target: pg);
                      Synth(\simpl, [\out,ot,\freq,frq,\amp,am*0.5,\md1,m1,\md2,m2,\rls,rl*1.1,\pos,ps], target: pg);
                    });
    });
    
    this.addCommand("rvset", "f", { arg msg;
      rvrb = Synth("Reverb", [\out,context.out_b,\fxindx,fx,\noff,fxroute.asMap], target:fxg, addAction: \addToTail);
		});
		
		this.addCommand("rv", "i", { arg msg; fxroute.set(msg[1]); first=msg[1];});
  }
}
