package ljr.xxx.sendbuildstatus;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.DependecyDeclarer;
import hudson.model.DependencyGraph;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import net.sf.json.JSONObject;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kohsuke.stapler.StaplerRequest;

public class SendStatus extends Notifier {
	
	private Integer crid;
//	private final String urlHead;

	public Integer getCrid() {
		return crid;
	}

	public void setCrid(Integer crid) {
		this.crid = crid;
	}

	@Override
	public boolean needsToRunAfterFinalized() {
		return true;
	}
	
	 @DataBoundConstructor
	public SendStatus() {
//		this.urlHead = urlHead;
		this.crid=crid;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		String url = getDescriptor().getUrlHead()+"s=lobxxx";
    	listener.getLogger().println("build status url:"+build.getResult());
    	HttpClient httpClient = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(url);
    	HttpResponse response = null;
    	try {
			response = httpClient.execute(httpGet);
//			listener.getLogger().println("response: "+response.getStatusLine().getStatusCode()+"!");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	if(response.getStatusLine().getStatusCode()==200){
			listener.getLogger().println("return this to scm platform!");
			return true;
		}else{
			listener.getLogger().println("can't return to scm platform , information:"+response.getStatusLine());
			return false;
		}
//		return super.perform(build, launcher, listener);
	}

	@Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

	@Extension
	public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		private String urlHead;
		
		 /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
		public DescriptorImpl() {
            load();
        }
		
		public String getUrlHead() {
	        return urlHead;
	    }
		
		
		@Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
			urlHead = formData.getString("urlHead");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req,formData);
        }
		
		@Override
		public String getDisplayName() {
			return "send build status";
		}

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
}
